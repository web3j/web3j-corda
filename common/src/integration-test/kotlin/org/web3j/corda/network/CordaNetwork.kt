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

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.idea.IdeaProject
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.protocol.NetworkMap
import org.web3j.corda.testcontainers.KGenericContainer
import org.web3j.corda.util.NonNullMap
import org.web3j.corda.util.OpenApiVersion.v3_0_1
import org.web3j.corda.util.isMac
import org.web3j.corda.util.toNonNullMap
import java.io.File
import java.nio.file.Files
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
     * Directory where the CorDapp JARs are located.
     */
    var baseDir: File = File("${System.getProperty("user.dir")}/build/libs")

    /**
     * The nodes in this network.
     */
    lateinit var nodes: NonNullMap<String, CordaNode>

    val map: NetworkMap by lazy {
        NetworkMap.build(CordaService("http://localhost:${mapContainer.getMappedPort(8080)}"))
    }

    /**
     * Dependencies of the [baseDir] CorDapp containing CorDapps JARs.
     * This is used to start the Braid server with all required endpoints.
     */
    internal val additionalPaths: List<String> by lazy {
        if (isGradleProject()) {
            connection.getModel(IdeaProject::class.java).modules.flatMap {
                it.dependencies
            }.filterIsInstance<IdeaSingleEntryLibraryDependency>()
                .filter {
                    it.gradleModuleVersion.group.startsWith("net.corda")
                }.map {
                    it.file.absolutePath
                }
        } else {
            // Not a valid Gradle project, use baseDir contents
            baseDir.listFiles()!!.map { it.absolutePath }
        }
    }

    /**
     * The internal Docker network.
     */
    internal val network = Network.newNetwork()

    /**
     * CorDapp Docker-mapped directory.
     */
    internal val cordappsDir = (if (isMac) "/private" else "") +
            Files.createTempDirectory("cordapps").toFile().absolutePath

    /**
     * Cordite network map Docker container.
     */
    private val mapContainer: KGenericContainer by lazy {
        KGenericContainer(NETWORK_MAP_IMAGE)
            .withCreateContainerCmdModifier {
                it.withHostName(NETWORK_MAP_ALIAS)
                it.withName(NETWORK_MAP_ALIAS)
            }.withNetwork(network)
            .withNetworkAliases(NETWORK_MAP_ALIAS)
            .withEnv(mapOf("NMS_STORAGE_TYPE" to "file"))
            .waitingFor(Wait.forHttp("").forPort(NETWORK_MAP_PORT))
            .apply { start() }
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

    private fun isGradleProject(): Boolean {
        return File(baseDir, "build.gradle").exists()
    }

    companion object {
        private const val NETWORK_MAP_IMAGE = "cordite/network-map:latest"
        private const val NETWORK_MAP_ALIAS = "networkmap"
        private const val NETWORK_MAP_PORT = 8080

        internal const val NETWORK_MAP_URL = "http://$NETWORK_MAP_ALIAS:$NETWORK_MAP_PORT"

        /**
         *  Corda network DSL entry point.
         */
        @JvmStatic
        @JvmName("network")
        fun networkJava(networkBlock: Consumer<CordaNetwork>): CordaNetwork {
            return CordaNetwork().apply {
                networkBlock.accept(this)
                if (isGradleProject()) {
                    createJarUsingGradle()
                }
            }
        }
    }
}
