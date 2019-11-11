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
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.idea.IdeaProject
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency
import org.testcontainers.containers.Network
import org.web3j.corda.network.CordaNetworkMap.Companion.DEFAULT_IMAGE
import org.web3j.corda.network.CordaNetworkMap.Companion.DEFAULT_TAG
import org.web3j.corda.network.CordaNetworkMap.Companion.ORGANIZATION
import org.web3j.corda.networkmap.NetworkMapApi
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.util.OpenApiVersion.v3_0_1
import org.web3j.corda.util.isMac
import org.web3j.corda.util.sanitizeCorDappName

/**
 * Corda network DSK for integration tests web3j CorDapp wrappers.
 */
@CordaDslMarker
class CordaNetwork private constructor() : ContainerCoordinates(
    ORGANIZATION, DEFAULT_IMAGE, DEFAULT_TAG
) {

    /**
     * Open API version.
     */
    var version = v3_0_1

    /**
     * Directory where the CorDapp JARs are located.
     */
    var baseDir: File = File(System.getProperty("user.dir"))

    /**
     * The nodes in this network.
     */
    lateinit var notaries: List<CordaNotaryNode>

    /**
     * The nodes in this network.
     */
    lateinit var parties: List<CordaPartyNode>

    /**
     * Client API to interact with this network.
     */
    val api: NetworkMapApi by lazy { map.instance.api }

    /**
     * Corda service for this network.
     */
    val service: CordaService by lazy { map.instance.service }

    /**
     * Make container tag settable.
     */
    override var tag = super.tag

    /**
     * The network map in this network.
     */
    internal lateinit var map: CordaNetworkMap

    /**
     * CorDapp Docker-mapped directory.
     */
    internal val cordappsDir: File by lazy {
        Files.createTempDirectory("cordapps_").apply {
            if (isGradleProject()) {
                // Copy project JARs into cordapps dir
                createJarUsingGradle(this)
                // FIXME Commented out causing all files copied
                // copyGradleDependencies(this)
            } else {
                // Not a valid Gradle project, copy baseDir
                baseDir.walkTopDown().forEach {
                    if (it.absolutePath.endsWith(".jar")) {
                        Files.copy(
                            it.toPath(),
                            File(toFile(), "${sanitizeCorDappName(it.name)}.jar").toPath(),
                            REPLACE_EXISTING
                        )
                    }
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
            parties = it.nodes
        }
    }

    /**
     * Build the CorDapp located in [baseDir] using the `jar` task and copy the resulting JAR into the given directory.
     */
    private fun createJarUsingGradle(cordappsDir: Path) {
        // Run the jar task to create the CorDapp JARs
        connection.newBuild().forTasks("jar").run()

        // Copy the built JAR artifacts into the CorDapps folder
        connection.getModel(IdeaProject::class.java).modules.map {
            File(it.gradleProject.buildDirectory, "libs")
        }.forEach { libsDir ->
            // FIXME Avoid copying sources and javadoc JARs, only copy artifacts
            libsDir.walkTopDown().forEach { file ->
                if (file.name.endsWith(".jar")) {
                    val destFile = File(cordappsDir.toFile(), "${sanitizeCorDappName(file.name)}.jar")
                    Files.copy(file.toPath(), destFile.toPath(), REPLACE_EXISTING)
                }
            }
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
                val destFile = File(cordappsDir.toFile(), it.file.name).toPath()
                Files.copy(it.file.toPath(), destFile, REPLACE_EXISTING)
            }
    }

    private fun isGradleProject(): Boolean {
        return File(baseDir, "build.gradle").exists()
    }

    companion object {
        /**
         *  Corda network DSL entry point.
         */
        @JvmStatic
        @JvmName("network")
        fun networkJava(networkBlock: Consumer<CordaNetwork>): CordaNetwork {
            return CordaNetwork().apply {
                networkBlock.accept(this)

                // Initialize a network map
                map = CordaNetworkMap(this)

                // Auto-start notaries and nodes
                (notaries + parties).onEach {
                    if (it.autoStart) { it.start() }
                }
            }
        }
    }
}
