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
import java.util.concurrent.CountDownLatch

class CordaNode internal constructor(private val network: CordaNetwork) {

    lateinit var name: String
    lateinit var location: String
    lateinit var country: String

    var userName: String = "user1"
    var password: String = "test"

    var p2pPort: Int = randomPort()
    var rpcPort: Int = randomPort()
    var adminPort: Int = randomPort()
    var apiPort: Int = randomPort()

    var isNotary: Boolean = false
    var autoStart: Boolean = true

    var timeOut: Long = Duration.ofMinutes(2).toMillis()

    val api: Corda by lazy {
        startBraid()
        Corda.build(CordaService("http://localhost:$apiPort"))
    }

    private val container: KGenericContainer by lazy {
        network.createContainer(this)
    }

    private val braid = BraidMain()

    fun start() = container.start()
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
     * Start Braid server synchronously on port [apiPort].
     */
    private fun startBraid() {
        val latch = CountDownLatch(1)
        braid.start(
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
