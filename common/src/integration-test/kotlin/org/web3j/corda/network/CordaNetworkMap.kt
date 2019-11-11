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

import org.testcontainers.containers.wait.strategy.Wait
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.protocol.NetworkMap
import org.web3j.corda.testcontainers.KGenericContainer

class CordaNetworkMap internal constructor(network: CordaNetwork) {

    /**
     * Container URL inside the Docker network.
     */
    internal val url = "http://$DEFAULT_IMAGE-${System.currentTimeMillis()}:$PORT"

    /**
     * Network Map instance to access the API.
     */
    internal val instance: NetworkMap by lazy {
        NetworkMap.build(CordaService("http://localhost:${container.ports[PORT]}"))
    }

    /**
     * Cordite network map Docker container.
     */
    private val container = KGenericContainer(network.toString())
        .withCreateContainerCmdModifier {
            it.withHostName(network.image)
            it.withName(network.image)
        }.withNetwork(network.network)
        .withNetworkAliases(network.image)
        .withEnv("NMS_STORAGE_TYPE", "file")
        .waitingFor(Wait.forHttp("").forPort(PORT))
        .withExposedPorts(PORT)
        .withLogConsumer {
            CordaNode.logger.info { it.utf8String.trimEnd() }
        }.apply { start() }

    companion object {
        internal const val ORGANIZATION = "cordite"
        internal const val DEFAULT_IMAGE = "network-map"
        internal const val DEFAULT_TAG = "v0.4.6"
        internal const val PORT = 8080
    }
}
