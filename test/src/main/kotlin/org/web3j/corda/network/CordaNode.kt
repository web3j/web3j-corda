/*
 * Copyright 2019 Web3 Labs Ltd.
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
import java.nio.file.Files
import java.time.Duration
import java.util.function.Consumer
import javax.security.auth.x500.X500Principal
import mu.KLogging
import org.testcontainers.containers.BindMode
import org.web3j.corda.testcontainers.KGenericContainer
import org.web3j.corda.util.canonicalName
import org.web3j.corda.util.randomPort

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
     * IP address of this Corda node, e.g. `notary:10006`.
     */
    val p2pAddress: String by lazy {
        "${container.containerIpAddress}:${container.ports[p2pPort]}"
    }

    /**
     * Corda P2P port for this node.
     */
    var p2pPort: Int = randomPort()

    /**
     * Corda RPC ports configuration.
     */
    val rpcSettings: CordaRpcSettings by lazy {
        CordaRpcSettings(this)
    }

    /**
     * Corda RPC users configuration.
     */
    val rpcUsers: CordaRpcUsers = CordaRpcUsers()

    /**
     * Start this node automatically?
     */
    var autoStart: Boolean = true

    /**
     * Timeout for this Corda node to start.
     */
    var timeOut: Duration = Duration.ofMinutes(5)

    /**
     * X.500 canonical name for this Corda node, eg. `notary-london-gb`.
     */
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
    internal val container: KGenericContainer by lazy {
        val nodeDir = File(network.cordappsDir, canonicalName).apply { mkdirs() }
        createNodeConfFile(nodeDir.resolve("node.conf"))
        saveCertificateFromNetworkMap(nodeDir)
        KGenericContainer(toString())
            .withNetwork(network.network)
            .withExposedPorts(p2pPort, rpcSettings.port, rpcSettings.adminPort)
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
                print(it.utf8String)
            }.apply {
                configure(nodeDir)
                logger.info { "Node $canonicalName environment variables: $envMap" }
                logger.info { "Node $canonicalName file system binds: $binds" }
            }
    }

    @JvmName("rpcSettings")
    fun rpcSettingsJava(rpcSettingsBlock: Consumer<CordaRpcSettings>) {
        rpcSettingsBlock.accept(rpcSettings)
    }

    @JvmName("rpcUsers")
    fun rpcUsersJava(rpcUsersBlock: Consumer<CordaRpcUsers>) {
        rpcUsersBlock.accept(rpcUsers)
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
        require(p2pPort.isPort()) { "Field 'p2pPort' is not a valid port" }
        require(rpcSettings.port.isPort()) { "Field 'rpcSettings.port' is not a valid port" }
        require(rpcSettings.adminPort.isPort()) { "Field 'rpcSettings.adminPort' is not a valid port" }
        require(rpcUsers.user.isNotBlank()) { "Field 'rpcUsers.user' cannot be blank" }
        require(rpcUsers.password.isNotBlank()) { "Field 'rpcUsers.password' cannot be blank" }
        require(rpcUsers.permissions.isNotEmpty()) { "Field 'rpcUsers.permissions' cannot be empty" }
    }

    protected abstract fun KGenericContainer.configure(nodeDir: File)

    private fun createNodeConfFile(file: File) {
        PrintWriter(OutputStreamWriter(FileOutputStream(file))).use {
            nodeConfTemplate.execute(
                mapOf(
                    "name" to name,
                    "isNotary" to (this is CordaNotaryNode),
                    "isValidating" to ((this is CordaNotaryNode) && validating),
                    "p2pAddress" to "$canonicalName:$p2pPort",
                    "rpcPort" to rpcSettings.port,
                    "adminPort" to rpcSettings.adminPort,
                    "user" to rpcUsers.user,
                    "password" to rpcUsers.password,
                    "permissions" to rpcUsers.permissions.joinToString(","),
                    "compatibilityZoneURL" to network.map.url
                ),
                it
            )
            it.flush()
        }
        logger.info { "Node $canonicalName generated node.conf file: \n${file.readText()}" }
    }

    private fun saveCertificateFromNetworkMap(nodeDir: File) {
        val certFolder = File(nodeDir, "certificates").apply { mkdirs() }
        val certFile = certFolder.resolve("network-root-truststore.jks")
        Files.write(certFile.toPath(), network.api.networkMap.truststore)
        logger.info {
            "Network Trust Root file (${certFile.lengthInKiloBytes} KB) saved at ${certFile.absolutePath}"
        }
    }

    private val File.lengthInKiloBytes: Long
        get() = length() / 1024

    companion object : KLogging() {
        private const val DEFAULT_ORGANIZATION = "corda"
        private const val DEFAULT_IMAGE = "corda-zulu-java1.8-4.3"
        private const val DEFAULT_TAG = "latest"

        private val portRange = 1024..65535

        @JvmStatic
        protected fun Int?.isPort() = this != null && portRange.contains(this)
    }
}
