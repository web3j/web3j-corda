package org.web3j.corda.protocol

import org.glassfish.jersey.client.proxy.WebResourceFactory

object ProxyBuilder {

    fun <T> build(type: Class<T>, service: CordaService): T {
        require(type.isInterface) { "Proxy class must be an interface" }

        val target = service.client.target(service.uri)
        return WebResourceFactory.newResource(type, target)
    }
}
