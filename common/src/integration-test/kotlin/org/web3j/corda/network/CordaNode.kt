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

class CordaNode internal constructor(private val network: CordaNetwork) {

    lateinit var name: String
    lateinit var location: String
    lateinit var country: String

    var p2pPort: Int = randomPort()
    var rpcPort: Int = randomPort()
    var adminPort: Int = randomPort()
    var apiPort: Int = randomPort()

    var isNotary: Boolean = false
    var autoStart: Boolean = true

    var timeOut: Long = Duration.ofMinutes(2).toMillis()

    val api: Corda by lazy {
        startServer().thenApply {
            Corda.build(CordaService("http://localhost:$apiPort"))
        }.get()
    }

    private val container: KGenericContainer by lazy {
        network.createContainer(this)
    }

    fun start() = container.start()
    fun stop() = container.stop()

    internal fun validate() {
        require(name.isNotBlank()) { "Field 'name' cannot be blank" }
        require(location.isNotBlank()) { "Field 'location' cannot be blank" }
        require(country.isNotBlank()) { "Field 'country' cannot be blank" }
        require(p2pPort.isPort()) { "Field 'p2pPort' is not a number between $portRange" }
        require(rpcPort.isPort()) { "Field 'rpcPort' is not a number between $portRange" }
        require(adminPort.isPort()) { "Field 'adminPort' is not a number between $portRange" }
    }

    private fun startServer() = runAsync {
        BraidMain().start(
            "localhost:${container.getMappedPort(rpcPort)}",
            "user1",
            "test",
            apiPort,
            network.version.toInt(),
            network.additionalPaths
        ).apply {
            do Thread.sleep(500) while (!isComplete)
            if (failed()) assertk.fail(cause().message ?: cause()::class.qualifiedName!!)
        }
    }

    companion object {
        private val portRange = 1024..65535

        private fun randomPort() = ServerSocket(0).localPort
        private fun Int?.isPort() = this != null && portRange.contains(this)
    }
}
