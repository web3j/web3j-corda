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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.nio.file.Files
import java.time.Duration
import javax.security.auth.x500.X500Principal
import mu.KLogging
import org.testcontainers.containers.BindMode
import org.web3j.corda.testcontainers.KGenericContainer
import org.web3j.corda.util.canonicalName

/**
 * Corda network node exposing a Corda API through a Braid container.
 */
@CordaDslMarker
abstract class CordaNode internal constructor(protected val network: CordaNetwork) : ContainerLifecycle,
    ContainerCoordinates(DEFAULT_ORGANIZATION, DEFAULT_IMAGE, DEFAULT_TAG) {
    /**
     * X.500 name for this Corda node, eg. `O=Notary, L=London, C=GB`.
     */
    lateinit var name: String

    /**
     * Corda P2P port for this node.
     */
    var p2pPort: Int = randomPort()

    /**
     * Corda RPC port for this node.
     */
    var rpcPort: Int = randomPort()

    /**
     * Admin port for this Corda node.
     */
    var adminPort: Int = randomPort()

    /**
     * Start this node automatically?
     */
    var autoStart: Boolean = true

    /**
     * Timeout for this Corda node to start.
     */
    var timeOut: Duration = Duration.ofMinutes(5)

    val canonicalName: String by lazy {
        require(::name.isInitialized)
        X500Principal(name).canonicalName
    }

    /**
     * Make container image settable.
     */
    override var image = super.image

    /**
     * Make container tag settable.
     */
    override var tag = super.tag

    /**
     * CorDapp `node.conf` template file.
     */
    private val nodeConfTemplate = CordaNetwork::class.java.classLoader
        .getResourceAsStream("node_conf.mustache")?.run {
            Mustache.compiler().compile(InputStreamReader(this))
        } ?: throw IllegalStateException("Template not found: node_conf.mustache")

    /**
     * Docker container instance for this node.
     */
    private val container: KGenericContainer by lazy {
        val nodeDir = File(network.cordappsDir, canonicalName).apply { mkdirs() }
        createNodeConfFiles(nodeDir.resolve("node.conf"))
        saveCertificateFromNetworkMap(nodeDir)
        logger.info { "Network map URL: ${network.map.url}" }
        KGenericContainer(toString())
            .withNetwork(network.network)
            .withExposedPorts(p2pPort, rpcPort, adminPort)
            .withFileSystemBind(
                nodeDir.absolutePath, "/etc/corda",
                BindMode.READ_WRITE
            ).withFileSystemBind(
                nodeDir.resolve("certificates").absolutePath,
                "/opt/corda/certificates",
                BindMode.READ_WRITE
            ).withEnv("NETWORKMAP_URL", network.map.url)
            .withEnv("DOORMAN_URL", network.map.url)
            .withEnv("NETWORK_TRUST_PASSWORD", "trustpass")
            .withEnv("MY_PUBLIC_ADDRESS", "http://localhost:$p2pPort")
            .withCommand("config-generator --generic")
            .withStartupTimeout(timeOut)
            .withCreateContainerCmdModifier {
                it.withHostName(canonicalName)
                it.withName(canonicalName)
            }.withLogConsumer {
                logger.info { it.utf8String.trimEnd() }
            }.apply {
                configure(nodeDir)
            }
    }

    /**
     * Start this Corda node.
     */
    override fun start() {
        logger.info("Starting Corda node $canonicalName...")
        container.start()
        logger.info("Started Corda node $canonicalName.")
    }

    /**
     * Stop this Corda node.
     */
    override fun stop() {
        logger.info("Stopping Corda node $canonicalName...")
        container.stop()
        logger.info("Stopped Corda node $canonicalName.")
    }

    internal open fun validate() {
        require(name.isNotBlank()) { "Field 'name' cannot be blank" }
        require(p2pPort.isPort()) { "Field 'p2pPort' is not in $portRange" }
        require(rpcPort.isPort()) { "Field 'rpcPort' is not in $portRange" }
        require(adminPort.isPort()) { "Field 'adminPort' is not in $portRange" }
    }

    protected abstract fun KGenericContainer.configure(nodeDir: File)

    private fun createNodeConfFiles(file: File) {
        PrintWriter(OutputStreamWriter(FileOutputStream(file))).use {
            nodeConfTemplate.execute(
                mapOf(
                    "name" to name,
                    "isNotary" to (this is CordaNotaryNode),
                    "isValidating" to ((this is CordaNotaryNode) && validating),
                    "p2pAddress" to "$canonicalName:$p2pPort",
                    "rpcPort" to rpcPort,
                    "adminPort" to adminPort,
                    "networkMapUrl" to network.map.url
                ),
                it
            )
            it.flush()
        }
    }

    private fun saveCertificateFromNetworkMap(nodeDir: File) {
        val certificateFolder = File(nodeDir, "certificates").apply { mkdirs() }
        val certificateFile = certificateFolder.resolve("network-root-truststore.jks")

        certificateFile.walkTopDown().forEach {
            logger.info {
                "${it.absolutePath} ${ if (it.canRead()) "r" else ""}" +
                    "${ if (it.canWrite()) "w" else ""}${ if (it.canExecute()) "x" else ""}"
            }
        }

        Files.write(certificateFile.toPath(), network.api.networkMap.truststore)
    }

    companion object : KLogging() {
        private const val DEFAULT_ORGANIZATION = "corda"
        private const val DEFAULT_IMAGE = "corda-zulu-4.1"
        private const val DEFAULT_TAG = "latest"

        private val portRange = 1024..65535

        @JvmStatic
        protected fun randomPort() = ServerSocket(0).use { it.localPort }

        @JvmStatic
        protected fun Int?.isPort() = this != null && portRange.contains(this)
    }
}
