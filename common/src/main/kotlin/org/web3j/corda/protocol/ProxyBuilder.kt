package org.web3j.corda.protocol

import org.glassfish.jersey.client.proxy.WebResourceFactory

abstract class ProxyBuilder<T> protected constructor(private val type: Class<T>) {

    fun build(service: CordaService): T {
        require(type.isInterface)

        val target = service.client.target(service.uri)
        return WebResourceFactory.newResource(type, target)
    }
}
