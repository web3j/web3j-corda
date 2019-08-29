package org.web3j.corda.api

import javax.ws.rs.client.ClientRequestContext
import javax.ws.rs.client.ClientRequestFilter
import javax.ws.rs.core.HttpHeaders

class AuthenticationFilter private constructor(private val token: String) : ClientRequestFilter {

    override fun filter(requestContext: ClientRequestContext) {
        requestContext.headers.putSingle(HttpHeaders.AUTHORIZATION, "Bearer $token")
    }

    companion object {
        fun token(token: String) = AuthenticationFilter(token)
    }
}
