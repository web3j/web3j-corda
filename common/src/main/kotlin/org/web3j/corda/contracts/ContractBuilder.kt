package org.web3j.corda.contracts

import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.web3j.corda.protocol.Corda

abstract class ContractBuilder<T> protected constructor(private val type: Class<T>) {

    fun build(corda: Corda): T {
        val target = corda.service.client.target(corda.service.uri)
        return WebResourceFactory.newResource(type, target)
    }
}
