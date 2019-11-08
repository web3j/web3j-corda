package org.web3j.corda.network

import org.testcontainers.containers.wait.strategy.Wait
import org.web3j.corda.networkmap.NetworkMapApi
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.protocol.NetworkMap
import org.web3j.corda.testcontainers.KGenericContainer

@CordaDslMarker
class CordaNetworkMap internal constructor(network: CordaNetwork) {

    /**
     * Host name inside the Docker container network.
     */
    private val name = "${NETWORK_MAP_IMAGE}-${System.currentTimeMillis()}"

    /**
     * Container URL inside the Docker network.
     */
    internal val url = "http://$name:${NETWORK_MAP_PORT}"

    /**
     * Docker image tag, by default `edge`.
     */
    var tag: String = NETWORK_MAP_DEFAULT_TAG

    /**
     * Network Map instance to access the API.
     */
    private val instance: NetworkMap by lazy {
        NetworkMap.build(CordaService("http://localhost:${container.getMappedPort(NETWORK_MAP_PORT)}"))
    }
    
    val api: NetworkMapApi = instance.api
    val service: CordaService = instance.service

    /**
     * Cordite network map Docker container.
     */
    private val container: KGenericContainer by lazy {
        KGenericContainer("$NETWORK_MAP_ORGANIZATION/$NETWORK_MAP_IMAGE:$tag")
            .withCreateContainerCmdModifier {
                it.withHostName(name)
                it.withName(name)
            }.withNetwork(network.network)
            .withNetworkAliases(name)
            .withEnv("NMS_STORAGE_TYPE", "file")
            .waitingFor(Wait.forHttp("").forPort(NETWORK_MAP_PORT))
            .withLogConsumer {
                CordaNode.logger.info { it.utf8String.trimEnd() }
            }.apply { start() }
    }

    companion object {
        private const val NETWORK_MAP_ORGANIZATION = "cordite"
        private const val NETWORK_MAP_DEFAULT_TAG = "edge"
        private const val NETWORK_MAP_IMAGE = "network-map"
        private const val NETWORK_MAP_PORT = 8080
    }
}