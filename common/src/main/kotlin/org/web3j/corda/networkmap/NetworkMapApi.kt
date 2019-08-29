package org.web3j.corda.networkmap

import org.web3j.corda.protocol.CordaService
import org.web3j.corda.protocol.ProxyBuilder
import javax.ws.rs.Path

/**
 * Network Map Service client.
 *
 * **Please note:** The protected parts of this API require JWT authentication.
 *
 * @see [AdminApi.login]
 */
interface NetworkMapApi {

    @get:Path("network-map")
    val networkMap: NetworkMap

    @get:Path("certificate")
    val certificate: Certificate

    @get:Path("certman/api")
    val certMan: CertManApi

    @get:Path("admin/api")
    val admin: AdminApi

    companion object {

        @JvmStatic
        fun build(service: CordaService): NetworkMapApi {
            return ProxyBuilder.build(NetworkMapApi::class.java, service)
        }

        @JvmStatic
        fun build(service: CordaService, token: String): NetworkMapApi {
            return ProxyBuilder.build(NetworkMapApi::class.java, service, token)
        }
    }
}
