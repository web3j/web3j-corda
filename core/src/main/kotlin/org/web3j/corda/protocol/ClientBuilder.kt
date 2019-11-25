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
package org.web3j.corda.protocol

import java.lang.reflect.Proxy
import javax.ws.rs.ClientErrorException
import mu.KLogging
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.web3j.corda.api.AuthenticationFilter

object ClientBuilder : KLogging() {

    /**
     * Builds a JAX-RS client with the given type [T].
     */
    fun <T> build(type: Class<T>, service: CordaService, token: String? = null): T {
        require(type.isInterface) { "Client class must be an interface" }

        val target = service.client.target(service.uri)
        token?.run { target.register(AuthenticationFilter.token(token)) }

        return WebResourceFactory.newResource(type, target)
    }

    /**
     * Builds a JAX-RS client which maps client errors to other exceptions.
     */
    fun <T> build(
        type: Class<T>,
        service: CordaService,
        mapper: (ClientErrorException) -> RuntimeException,
        token: String? = null
    ): T {
        val client = build(type, service, token)
        val handler = ClientErrorHandler(client, mapper)

        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(type.classLoader, arrayOf(type), handler) as T
    }
}
