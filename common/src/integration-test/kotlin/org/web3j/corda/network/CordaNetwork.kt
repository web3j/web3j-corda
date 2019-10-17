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

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.function.Consumer
import kotlin.streams.toList
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.idea.IdeaProject
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.protocol.NetworkMap
import org.web3j.corda.testcontainers.KGenericContainer
import org.web3j.corda.util.OpenApiVersion.v3_0_1
import org.web3j.corda.util.isMac

/**
 * Corda network DSK for integration tests web3j CorDapp wrappers.
 */
class CordaNetwork private constructor() {

    /**
     * Open API version.
     */
    var version = v3_0_1

    /**
     * Directory where the CorDapp JARs are located.
     */
    var baseDir: File = File("${System.getProperty("user.dir")}/build/libs")

    /**
     * The nodes in this network.
     */
    lateinit var notaries: List<CordaNotaryNode>

    /**
     * The nodes in this network.
     */
    lateinit var nodes: List<CordaPartyNode>

    val map: NetworkMap by lazy {
        NetworkMap.build(CordaService("http://localhost:${mapContainer.getMappedPort(8080)}"))
    }

    /**
     * CorDapp Docker-mapped directory.
     */
    internal val cordappsDir: File by lazy {
        Files.createTempDirectory("cordapps").apply {
            if (isGradleProject()) {
                // Copy project JARs into cordapps dir
                createJarUsingGradle(this)
                copyGradleDependencies(this)
            } else {
                // Not a valid Gradle project, copy baseDir
                Files.list(baseDir.toPath()).forEach {
                    Files.copy(it, File(toFile(), it.toFile().name).toPath(), REPLACE_EXISTING)
                }
            }
        }.toFile().absolutePath.run {
            // Fix Mac temporary folder absolute path
            File((if (isMac) "/private" else "") + this)
        }
    }

    /**
     * The internal Docker network.
     */
    internal val network = Network.newNetwork()

    internal val mapUrl: String
        get() = "http://$mapName:$NETWORK_MAP_PORT"

    private val mapName: String
        get() = "$NETWORK_MAP_ALIAS-${System.currentTimeMillis()}"

    /**
     * Cordite network map Docker container.
     */
    private val mapContainer: KGenericContainer by lazy {
        KGenericContainer(NETWORK_MAP_IMAGE)
            .withCreateContainerCmdModifier {
                it.withHostName(mapName)
                it.withName(mapName)
            }.withNetwork(network)
            .withNetworkAliases(mapName)
            .withEnv("NMS_STORAGE_TYPE", "file")
            .waitingFor(Wait.forHttp("").forPort(NETWORK_MAP_PORT))
            .withLogConsumer {
                CordaNode.logger.info { it.utf8String.trimEnd() }
            }.apply { start() }
    }

    /**
     * Gradle connection to the CorDapp located in [baseDir].
     */
    private val connection: ProjectConnection by lazy {
        GradleConnector.newConnector()
            .useBuildDistribution()
            .forProjectDirectory(baseDir)
            .connect()
    }

    /**
     * Defines a node in this network.
     */
    @JvmName("nodes")
    fun nodesJava(nodesBlock: Consumer<CordaNodes>) {
        CordaNodes(this).apply {
            nodesBlock.accept(this)
        }.also {
            notaries = it.notaries
            nodes = it.nodes
        }
    }

    /**
     * Build the CorDapp located in [baseDir] using the `jar` task and copy the resulting JAR into the given directory.
     */
    private fun createJarUsingGradle(cordappsDir: Path) {
        // Run the jar task to create the CorDapp JAR
        connection.newBuild().forTasks("jar").run()

        // Copy Gradle project JAR into cordapps folder
        Files.list(File(baseDir, "build/libs").toPath()).toList().forEach {
            Files.copy(it, File(cordappsDir.toFile(), it.toFile().name).toPath(), REPLACE_EXISTING)
        }
    }

    /**
     * Resolve and copy Gradle project dependencies into the given directory.
     */
    private fun copyGradleDependencies(cordappsDir: Path) {
        connection.getModel(IdeaProject::class.java).modules.flatMap {
            it.dependencies
        }.filterIsInstance<IdeaSingleEntryLibraryDependency>()
            .filter {
                it.gradleModuleVersion.group.startsWith("net.corda")
            }.forEach {
                Files.copy(it.file.toPath(), File(cordappsDir.toFile(), it.file.name).toPath(), REPLACE_EXISTING)
            }
    }

    private fun isGradleProject(): Boolean {
        return File(baseDir, "build.gradle").exists()
    }

    companion object {
        private const val NETWORK_MAP_IMAGE = "cordite/network-map:latest"
        private const val NETWORK_MAP_ALIAS = "networkmap"
        private const val NETWORK_MAP_PORT = 8080

        /**
         *  Corda network DSL entry point.
         */
        @JvmStatic
        @JvmName("network")
        fun networkJava(networkBlock: Consumer<CordaNetwork>): CordaNetwork {
            return CordaNetwork().apply {
                networkBlock.accept(this)
            }
        }
    }
}
