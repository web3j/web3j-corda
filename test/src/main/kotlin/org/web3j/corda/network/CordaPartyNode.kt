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

import java.io.File
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.wait.strategy.Wait
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.testcontainers.KGenericContainer
import org.web3j.corda.util.randomPort

/**
 * Corda network node exposing a Corda API through a Braid container.
 */
class CordaPartyNode internal constructor(network: CordaNetwork) : CordaNode(network) {

    /**
     * Braid server API port.
     */
    var webPort: Int = randomPort()

    /**
     * Braid server API address.
     */
    val webAddress: String by lazy {
        "${braid.containerIpAddress}:${braid.ports[webPort]}"
    }

    /**
     * Corda API to interact with this node.
     */
    val corda: Corda by lazy {
        Corda.build(CordaService("http://$webAddress"))
    }

    /**
     * Braid server for this Corda node.
     */
    private val braid: KGenericContainer by lazy {
        val braidName = "$BRAID_ALIAS-$canonicalName"
        KGenericContainer(BRAID_IMAGE)
            .withCreateContainerCmdModifier {
                it.withHostName(braidName)
                it.withName(braidName)
            }.withFileSystemBind(
                network.cordappsDir.absolutePath,
                "/opt/braid/cordapps",
                BindMode.READ_WRITE
            ).withNetwork(network.network)
            .withExposedPorts(webPort)
            .withNetworkAliases(braidName)
            .withEnv("NODE_RPC_ADDRESS", "$canonicalName:${rpcSettings.port}")
            .withEnv("NODE_RPC_USERNAME", rpcUsers.user)
            .withEnv("NODE_RPC_PASSWORD", rpcUsers.password)
            .withEnv("PORT", "$webPort")
            .waitingFor(Wait.forHttp("").forPort(webPort))
            .withLogConsumer {
                logger.info { it.utf8String.trimEnd() }
            }
    }

    /**
     * Start this Corda node.
     */
    override fun start() {
        super.start()
        logger.info("Starting Braid node $canonicalName...")
        braid.start()
        logger.info("Started Braid node $canonicalName.")
    }

    /**
     * Stop this Corda node.
     */
    override fun stop() {
        super.stop()
        logger.info("Stopping Braid node $canonicalName...")
        braid.stop()
        logger.info("Stopped Braid node $canonicalName.")
    }

    override fun validate() {
        require(webPort.isPort()) { "Field 'webPort' is not a valid port" }
        super.validate()
    }

    override fun KGenericContainer.configure(nodeDir: File) {
        withFileSystemBind(
            this@CordaPartyNode.network.cordappsDir.absolutePath,
            "/opt/corda/cordapps",
            BindMode.READ_WRITE
        )
    }

    companion object {
        private const val BRAID_IMAGE = "cordite/braid:v4.1.2-RC13"
        private const val BRAID_ALIAS = "braid"
    }
}
