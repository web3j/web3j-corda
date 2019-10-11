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
import io.bluebank.braid.corda.server.BraidMain
import io.vertx.core.Future
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.util.concurrent.CountDownLatch
import javax.security.auth.x500.X500Principal
import mu.KLogging
import org.testcontainers.containers.BindMode
import org.web3j.corda.networkmap.LoginRequest
import org.web3j.corda.networkmap.NotaryType.NON_VALIDATING
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.protocol.NetworkMap
import org.web3j.corda.testcontainers.KGenericContainer
import org.web3j.corda.util.canonicalName
import org.web3j.corda.util.isMac

class CordaNode internal constructor(private val network: CordaNetwork) {

    /**
     * X.500 name for this Corda node, eg. `O=Notary, L=London, C=GB`.
     */
    lateinit var name: String

    /**
     * Braid server username.
     */
    var userName: String = "user1"

    /**
     * Braid server password.
     */
    var password: String = "test"

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
     * Braid server API port.
     */
    var apiPort: Int = randomPort()

    /**
     * Is this node a notary?
     */
    var isNotary: Boolean = false

    /**
     * Start this node automatically?
     */
    var autoStart: Boolean = true

    /**
     * Timeout for this Corda node to start.
     */
    var timeOut: Long = Duration.ofMinutes(5).toMillis()

    /**
     * Corda API to interact with this node.
     */
    val api: Corda by lazy {
        Corda.build(CordaService("http://localhost:$apiPort"))
    }

    val canonicalName: String by lazy {
        require(::name.isInitialized)
        X500Principal(name).canonicalName
    }

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

        val tempDir = (if (isMac) "/private" else "") +
                Files.createTempDirectory("tempCordapps").toFile().absolutePath

        network.additionalPaths.forEach {
            val path = File(it).toPath()
            Files.copy(path, File("$tempDir/${path.toFile().name}").toPath())
        }

        KGenericContainer(CORDA_ZULU_IMAGE)
            .withNetwork(network.network)
            .withExposedPorts(p2pPort, rpcPort, adminPort)
            .withFileSystemBind(
                nodeDir.absolutePath, "/etc/corda",
                BindMode.READ_WRITE
            ).withFileSystemBind(
                nodeDir.resolve("certificates").absolutePath,
                "/opt/corda/certificates",
                BindMode.READ_WRITE
            ).withEnv("NETWORKMAP_URL", CordaNetwork.NETWORK_MAP_URL)
            .withEnv("DOORMAN_URL", CordaNetwork.NETWORK_MAP_URL)
            .withEnv("NETWORK_TRUST_PASSWORD", "trustpass")
            .withEnv("MY_PUBLIC_ADDRESS", "http://localhost:$p2pPort")
            .withCommand("config-generator --generic")
            .withStartupTimeout(Duration.ofMillis(timeOut))
            .withCreateContainerCmdModifier {
                it.withHostName(canonicalName.toLowerCase())
                it.withName(canonicalName.toLowerCase())
            }.withLogConsumer {
                logger.info { it.utf8String.trimEnd() }
            }.apply {
                if (isNotary) {
                    logger.info("Starting notary container $canonicalName...")
                    start()
                    logger.info("Started notary container $canonicalName.")
                    extractNotaryNodeInfo(this, nodeDir).also {
                        updateNotaryInNetworkMap(nodeDir.resolve(it).absolutePath)
                    }
                    logger.info("Stopping notary container $canonicalName...")
                    stop()
                    logger.info("Stopped notary container $canonicalName.")
                } else {
                    withFileSystemBind(
                        tempDir,
                        "/opt/corda/cordapps",
                        BindMode.READ_WRITE
                    )
                }
            }
    }

    /**
     * Braid server for this Corda node.
     */
    private val braid = BraidMain(
        network.jarsClassLoader,
        network.version.toInt(),
        network.vertx
    )

    /**
     * Start this Corda node.
     */
    fun start() {
        logger.info("Starting Corda node $canonicalName...")
        container.start()
        braid.start()
        logger.info("Started Corda node $canonicalName.")
    }

    /**
     * Stop this Corda node.
     */
    fun stop() {
        logger.info("Stopping Corda node $canonicalName...")
        braid.shutdown()
        container.stop()
        logger.info("Stopped Corda node $canonicalName.")
    }

    internal fun validate() {
        require(name.isNotBlank()) { "Field 'name' cannot be blank" }
        require(userName.isNotBlank()) { "Field 'userName' cannot be blank" }
        require(password.isNotBlank()) { "Field 'password' cannot be blank" }
        require(p2pPort.isPort()) { "Field 'p2pPort' is not in $portRange" }
        require(rpcPort.isPort()) { "Field 'rpcPort' is not in $portRange" }
        require(adminPort.isPort()) { "Field 'adminPort' is not in $portRange" }
    }

    private fun createNodeConfFiles(file: File) {
        PrintWriter(OutputStreamWriter(FileOutputStream(file))).use {
            nodeConfTemplate.execute(
                mapOf(
                    "name" to name,
                    "isNotary" to isNotary,
                    "p2pPort" to p2pPort,
                    "rpcPort" to rpcPort,
                    "adminPort" to adminPort,
                    "networkMapUrl" to CordaNetwork.NETWORK_MAP_URL
                ),
                it
            )
            it.flush()
        }
    }

    private fun saveCertificateFromNetworkMap(nodeDir: File) {
        val certificateFolder = File(nodeDir, "certificates").apply { mkdir() }
        val certificateFile = certificateFolder.resolve("network-root-truststore.jks")
        Files.write(certificateFile.toPath(), network.map.networkMap.truststore)
    }

    private fun extractNotaryNodeInfo(notary: KGenericContainer, notaryNodeDir: File): String {
        logger.info("Extracting notary info from container $canonicalName...")
        return notary.run {
            execInContainer("find", ".", "-maxdepth", "1", "-name", "nodeInfo*").stdout.run {
                substring(2, length - 1) // remove relative path and the ending newline character
            }.also {
                val nodeInfoPath = notaryNodeDir.resolve(it).absolutePath
                logger.info("Copying notary folder from /opt/corda/$it to $nodeInfoPath")
                copyFileFromContainer("/opt/corda/$it", nodeInfoPath)
                logger.info("Removing folder from /opt/corda/$it")
                execInContainer("rm", "network-parameters")
                logger.info("Extracted notary info from container $canonicalName.")
            }
        }
    }

    private fun updateNotaryInNetworkMap(nodeInfoPath: String) {
        val loginRequest = LoginRequest("sa", "admin")
        val token = network.map.admin.login(loginRequest)

        val authMap = NetworkMap.build(CordaService(network.map.service.uri), token)

        logger.info("Creating a non-validating notary in network map with node info $nodeInfoPath")
        authMap.admin.notaries.create(NON_VALIDATING, Files.readAllBytes(Paths.get(nodeInfoPath)))
    }

    /**
     * Start Braid server synchronously.
     */
    private fun BraidMain.start() {
        val latch = CountDownLatch(1)
        val result = Future.future<String>()
        start(
            "localhost:${container.getMappedPort(rpcPort)}",
            userName,
            password,
            apiPort
        ).setHandler {
            result.handle(it)
            latch.countDown()
        }
        latch.await()
        if (result.failed()) {
            assertk.fail(result.cause().message ?: result.cause()::class.qualifiedName!!)
        }
    }

    companion object : KLogging() {
        private const val CORDA_ZULU_IMAGE = "corda/corda-zulu-4.1:latest"

        private val portRange = 1024..65535

        private fun randomPort() = ServerSocket(0).use { it.localPort }
        private fun Int?.isPort() = this != null && portRange.contains(this)
    }
}
