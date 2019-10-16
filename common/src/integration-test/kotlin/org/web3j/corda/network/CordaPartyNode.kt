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
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.wait.strategy.Wait
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.testcontainers.KGenericContainer

/**
 * Corda network node exposing a Corda API through a Braid container.
 */
class CordaPartyNode internal constructor(network: CordaNetwork) : CordaNode(network) {

    /**
     * Braid server username.
     */
    var userName: String = "user1"

    /**
     * Braid server password.
     */
    var password: String = "test"

    /**
     * Braid server API port.
     */
    var apiPort: Int = randomPort()

    /**
     * Corda API to interact with this node.
     */
    val api: Corda by lazy {
        Corda.build(CordaService("http://localhost:${braid.ports[apiPort]}"))
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
            .withExposedPorts(apiPort)
            .withNetworkAliases(braidName)
            .withEnv("NODE_RPC_ADDRESS", "$canonicalName:$rpcPort")
            .withEnv("NODE_RPC_USERNAME", userName)
            .withEnv("NODE_RPC_PASSWORD", password)
            .withEnv("PORT", "$apiPort")
            .waitingFor(Wait.forHttp("").forPort(apiPort))
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
        require(userName.isNotBlank()) { "Field 'userName' cannot be blank" }
        require(password.isNotBlank()) { "Field 'password' cannot be blank" }
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
        private const val BRAID_IMAGE = "cordite/braid:edge"
        private const val BRAID_ALIAS = "braid"
    }
}
