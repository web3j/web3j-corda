package org.web3j.corda.protocol

import org.web3j.corda.api.CordaApi

class Corda private constructor(
    private val api: CordaApi,
    val service: CordaService
) : CordaApi by api {

    companion object {
        @JvmStatic
        fun build(service: CordaService): Corda {
            return Corda(ProxyBuilder.build(CordaApi::class.java, service), service)
        }
    }
}
