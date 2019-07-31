package org.web3j.corda.protocol

import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.web3j.corda.api.CordaApi

class Corda private constructor(
    private val api: CordaApi,
    val service: CordaService
) : CordaApi by api {

    companion object {
        fun build(service: CordaService): Corda {
            val target = service.client.target(service.uri)
            return Corda(WebResourceFactory.newResource(CordaApi::class.java, target), service)
        }
    }
}
