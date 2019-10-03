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

import io.bluebank.braid.corda.server.BraidMain
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.testcontainers.KGenericContainer
import java.net.ServerSocket
import java.time.Duration
import java.util.concurrent.CompletableFuture.runAsync
import java.util.concurrent.CountDownLatch

class CordaNode internal constructor(private val network: CordaNetwork) {

    /**
     * Name for this Corda node, eg. `PartyA`.
     */
    lateinit var name: String

    /**
     * Location for this Corda node, eg. `London`.
     */
    lateinit var location: String

    /**
     * Country for this Corda node, eg. `GB`.
     */
    lateinit var country: String

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

    /**
     * Docker container instance for this node.
     */
    private val container: KGenericContainer by lazy {
        network.createContainer(this)
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
        require(location.isNotBlank()) { "Field 'location' cannot be blank" }
        require(country.isNotBlank()) { "Field 'country' cannot be blank" }
        require(p2pPort.isPort()) { "Field 'p2pPort' is not in $portRange" }
        require(rpcPort.isPort()) { "Field 'rpcPort' is not in $portRange" }
        require(adminPort.isPort()) { "Field 'adminPort' is not in $portRange" }
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

    companion object {
        private val portRange = 1024..65535

        private fun randomPort() = ServerSocket(0).localPort
        private fun Int?.isPort() = this != null && portRange.contains(this)
    }
}
