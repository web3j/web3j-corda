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
import mu.KLogging
import org.testcontainers.containers.BindMode
import org.web3j.corda.networkmap.LoginRequest
import org.web3j.corda.networkmap.NotaryType
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.protocol.NetworkMap
import org.web3j.corda.testcontainers.KGenericContainer
import org.web3j.corda.util.canonicalName
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.util.concurrent.CompletableFuture.runAsync
import java.util.concurrent.CountDownLatch
import javax.security.auth.x500.X500Principal

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
    var timeOut: Long = Duration.ofMinutes(2).toMillis()

    /**
     * Corda API to interact with this node.
     */
    val api: Corda by lazy {
        braid.start().thenApply {
            Corda.build(CordaService("http://localhost:$apiPort"))
        }.get()
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
            .withEnv("MY_PUBLIC_ADDRESS", "http://localhost:${p2pPort}")
            .withCommand("config-generator --generic")
            .withStartupTimeout(Duration.ofMillis(timeOut))
            .withCreateContainerCmdModifier {
                it.withHostName(canonicalName.toLowerCase())
                it.withName(canonicalName.toLowerCase())
            }.withLogConsumer {
                logger.info { it.utf8String.trimEnd() }
            }.apply {
                if (isNotary) {
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

    /**
     * Braid server for this Corda node.
     */
    private val braid = BraidMain()

    /**
     * Start this Corda node.
     */
    fun start() = container.start()

    /**
     * Stop this Corda node.
     */
    fun stop() = container.stop()

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
        val networkMapUrl = "http://localhost:${network.networkMap.getMappedPort(8080)}"

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
        val networkMapUrl = "http://localhost:${network.networkMap.getMappedPort(8080)}"
        var networkMapApi = NetworkMap.build(CordaService(networkMapUrl))

        val loginRequest = LoginRequest("sa", "admin")
        val token = networkMapApi.admin.login(loginRequest)

        networkMapApi = NetworkMap.build(CordaService(networkMapUrl), token)
        networkMapApi.admin.notaries.create(NotaryType.NON_VALIDATING, Files.readAllBytes(Paths.get(nodeInfoPath)))
    }

    /**
     * Start Braid server synchronously.
     */
    private fun BraidMain.start() = runAsync {
        val latch = CountDownLatch(1)
        start(
            "localhost:${container.getMappedPort(rpcPort)}",
            userName,
            password,
            apiPort,
            network.version.toInt(),
            network.additionalPaths
        ).setHandler {
            if (it.failed()) {
                assertk.fail(it.cause().message ?: it.cause()::class.qualifiedName!!)
            } else {
                latch.countDown()
            }
        }
        latch.await()
    }

    companion object : KLogging() {
        private const val CORDA_ZULU_IMAGE = "corda/corda-zulu-4.1:latest"

        private val portRange = 1024..65535

        private fun randomPort() = ServerSocket(0).localPort
        private fun Int?.isPort() = this != null && portRange.contains(this)
    }
}
