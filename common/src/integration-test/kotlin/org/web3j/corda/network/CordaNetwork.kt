/*
 * Copyright 2019 Web3 Labs LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.corda.network

import com.samskivert.mustache.Mustache
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.idea.IdeaProject
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency
import org.junit.platform.commons.logging.LoggerFactory
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.web3j.corda.networkmap.LoginRequest
import org.web3j.corda.networkmap.NotaryType
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.protocol.NetworkMap
import org.web3j.corda.testcontainers.KGenericContainer
import org.web3j.corda.util.NonNullMap
import org.web3j.corda.util.OpenApiVersion.v3_0_1
import org.web3j.corda.util.isMac
import org.web3j.corda.util.toNonNullMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.util.function.Consumer
import kotlin.streams.toList

/**
 * Corda network DSK for integration tests web3j CorDapp wrappers.
 */
class CordaNetwork private constructor() {

    /**
     * Open API version.
     */
    var version = v3_0_1

    /**
     * CorDapp base directory.
     */
    lateinit var baseDir: File

    /**
     * The nodes in this network.
     */
    lateinit var nodes: NonNullMap<String, CordaNode>

    /**
     * Dependencies of the [baseDir] CorDapp containing CorDapps JARs.
     * This is used to start the Braid server with all required endpoints.
     */
    internal val additionalPaths: List<String> by lazy {
        if (isGradleProject()) {
            connection.getModel(IdeaProject::class.java).modules.flatMap {
                it.dependencies
            }.map {
                it as IdeaSingleEntryLibraryDependency
            }.filter {
                it.gradleModuleVersion.group.startsWith("net.corda")
            }.map {
                it.file.absolutePath
            }
        } else {
            // Not a valid Gradle project
            listOf<String>()
        }
    }

    /**
     * The internal Docker network.
     */
    private val network = Network.newNetwork()

    /**
     * CorDapp Docker-mapped directory.
     */
    private val cordappsDir = (if (isMac) "/private" else "") +
            Files.createTempDirectory("cordapps").toFile().absolutePath

    /**
     * CorDapp `node.conf` template file.
     */
    private val nodeConfTemplate = CordaNetwork::class.java.classLoader
        .getResourceAsStream("node_conf.mustache")?.run {
            Mustache.compiler().compile(InputStreamReader(this))
        } ?: throw IllegalStateException("Template not found: node_conf.mustache")

    /**
     * Gradle connection to the CorDapp located in [baseDir].
     */
    private val connection: ProjectConnection by lazy {
        require(this::baseDir.isInitialized)
        GradleConnector.newConnector()
            .useBuildDistribution()
            .forProjectDirectory(baseDir)
            .connect()
    }

    /**
     * Cordite network map Docker container.
     */
    private val networkMap: KGenericContainer by lazy {
        KGenericContainer(NETWORK_MAP_IMAGE)
            .withCreateContainerCmdModifier {
                it.withHostName(NETWORK_MAP_ALIAS)
                it.withName(NETWORK_MAP_ALIAS)
            }.withNetwork(network)
            .withNetworkAliases(NETWORK_MAP_ALIAS)
            .withEnv(mapOf("NMS_STORAGE_TYPE" to "file"))
            .waitingFor(Wait.forHttp("").forPort(8080))
            .apply { start() }
    }

    /**
     * Defines a node in this network.
     */
    @JvmName("nodes")
    fun nodesJava(nodesBlock: Consumer<CordaNodes>) {
        CordaNodes(this).apply {
            nodesBlock.accept(this)
            this@CordaNetwork.nodes = map {
                it.name to it
            }.toNonNullMap()
        }
    }

    /**
     * Build the CorDapp located in [baseDir] using the `jar` task.
     */
    private fun createJarUsingGradle() {
        // Run the jar task to create the CorDapp JAR
        connection.newBuild().forTasks("jar").run()

        // Copy JARs into corDapps folder
        Files.list(File(baseDir, "build/libs").toPath()).toList().forEach {
            Files.copy(it, File(cordappsDir, it.toFile().name).toPath())
        }
    }

    /**
     * Create a Docker container for the given Corda node.
     */
    internal fun createContainer(node: CordaNode): KGenericContainer {

        val nodeDir = File(cordappsDir, node.name).apply { mkdirs() }
        createNodeConfFiles(node, nodeDir.resolve("node.conf"))
        saveCertificateFromNetworkMap(nodeDir)

        return KGenericContainer(CORDA_ZULU_IMAGE)
            .withNetwork(network)
            .withExposedPorts(node.p2pPort, node.rpcPort, node.adminPort)
            .withFileSystemBind(
                nodeDir.absolutePath, "/etc/corda",
                BindMode.READ_WRITE
            ).withFileSystemBind(
                nodeDir.resolve("certificates").absolutePath,
                "/opt/corda/certificates",
                BindMode.READ_WRITE
            ).withEnv("NETWORKMAP_URL", NETWORK_MAP_URL)
            .withEnv("DOORMAN_URL", NETWORK_MAP_URL)
            .withEnv("NETWORK_TRUST_PASSWORD", "trustpass")
            .withEnv("MY_PUBLIC_ADDRESS", "http://localhost:${node.p2pPort}")
            .withCommand("config-generator --generic")
            .withStartupTimeout(Duration.ofMillis(node.timeOut))
            .withCreateContainerCmdModifier {
                it.withHostName(node.name.toLowerCase())
                it.withName(node.name.toLowerCase())
            }.withLogConsumer {
                logger.info { it.utf8String }
            }.apply {
                if (node.isNotary) {
                    start()
                    extractNotaryNodeInfo(this, nodeDir).also {
                        updateNotaryInNetworkMap(nodeDir.resolve(it).absolutePath)
                    }
                    stop()
                } else {
                    withFileSystemBind(
                        nodeDir.resolve("cordapps").absolutePath,
                        "/opt/corda/cordapps",
                        BindMode.READ_WRITE
                    )
                }
            }
    }

    private fun isGradleProject(): Boolean {
        return File(baseDir, "build.gradle").exists()
    }

    private fun createNodeConfFiles(node: CordaNode, file: File) {
        PrintWriter(OutputStreamWriter(FileOutputStream(file))).use {
            node.apply {
                this@CordaNetwork.nodeConfTemplate.execute(
                    mapOf(
                        "name" to name,
                        "isNotary" to isNotary,
                        "location" to location,
                        "country" to country,
                        "p2pPort" to p2pPort,
                        "rpcPort" to rpcPort,
                        "adminPort" to adminPort,
                        "networkMapUrl" to NETWORK_MAP_URL
                    ),
                    it
                )
            }
            it.flush()
        }
    }

    private fun saveCertificateFromNetworkMap(nodeDir: File) {
        val certificateFolder = File(nodeDir, "certificates").apply { mkdir() }
        val certificateFile = certificateFolder.resolve("network-root-truststore.jks")
        val networkMapUrl = "http://localhost:${networkMap.getMappedPort(8080)}"

        NetworkMap.build(CordaService(networkMapUrl)).apply {
            Files.write(certificateFile.toPath(), networkMap.truststore)
        }
    }

    private fun extractNotaryNodeInfo(notary: KGenericContainer, notaryNodeDir: File): String {
        return notary.run {
            execInContainer("find", ".", "-maxdepth", "1", "-name", "nodeInfo*").stdout.run {
                substring(2, length - 1) // remove relative path and the ending newline character
            }.also {
                copyFileFromContainer("/opt/corda/$it", notaryNodeDir.resolve(it).absolutePath)
                execInContainer("rm", "network-parameters")
            }
        }
    }

    private fun updateNotaryInNetworkMap(nodeInfoPath: String) {
        val networkMapUrl = "http://localhost:${networkMap.getMappedPort(8080)}"
        var networkMapApi = NetworkMap.build(CordaService(networkMapUrl))

        val loginRequest = LoginRequest("sa", "admin")
        val token = networkMapApi.admin.login(loginRequest)

        networkMapApi = NetworkMap.build(CordaService(networkMapUrl), token)
        networkMapApi.admin.notaries.create(NotaryType.NON_VALIDATING, Files.readAllBytes(Paths.get(nodeInfoPath)))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CordaNetwork::class.java)

        private const val NETWORK_MAP_ALIAS = "networkmap"
        private const val NETWORK_MAP_URL = "http://$NETWORK_MAP_ALIAS:8080"

        private const val NETWORK_MAP_IMAGE = "cordite/network-map:latest"
        private const val CORDA_ZULU_IMAGE = "corda/corda-zulu-4.1:latest"

        /**
         *  Corda network DSL entry point.
         */
        @JvmStatic
        @JvmName("network")
        fun networkJava(networkBlock: Consumer<CordaNetwork>): CordaNetwork {
            return CordaNetwork().also {
                networkBlock.accept(it)
                if (it.isGradleProject()) {
                    it.createJarUsingGradle()
                }
            }
        }
    }
}
