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

import mu.KLogging
import org.testcontainers.containers.wait.strategy.Wait
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.protocol.NetworkMap
import org.web3j.corda.testcontainers.KGenericContainer

class CordaNetworkMap internal constructor(network: CordaNetwork) : ContainerLifecycle {

    /**
     * Container host name the Docker network.
     */
    private val host = "$DEFAULT_IMAGE-${System.currentTimeMillis()}"

    /**
     * Container URL inside the Docker network.
     */
    internal val url = "http://$host:$PORT"

    /**
     * Network Map instance to access the API.
     */
    internal val instance: NetworkMap by lazy {
        NetworkMap.build(CordaService("http://${container.containerIpAddress}:${container.ports[PORT]}"))
    }

    /**
     * Cordite network map Docker container.
     */
    private val container: KGenericContainer by lazy {
        KGenericContainer(network.toString())
            .withCreateContainerCmdModifier {
                it.withHostName(host)
                it.withName(host)
            }.withNetwork(network.network)
            .withNetworkAliases(host)
            .withEnv("NMS_STORAGE_TYPE", "file")
            .waitingFor(Wait.forHttp("").forPort(PORT))
            .withExposedPorts(PORT)
            .withLogConsumer {
                print(it.utf8String)
            }
    }

    /**
     * Start this network map.
     */
    override fun start() {
        logger.info("Starting network map...")
        container.start()
        CordaNode.logger.info("Started network map at $url")
    }

    /**
     * Stop this network map.
     */
    override fun stop() {
        logger.info("Stopping network map...")
        container.stop()
        logger.info("Stopped network map.")
    }

    companion object : KLogging() {
        internal const val DEFAULT_ORGANIZATION = "cordite"
        internal const val DEFAULT_IMAGE = "network-map"
        internal const val DEFAULT_TAG = "v0.5.0"
        internal const val PORT = 8080
    }
}
