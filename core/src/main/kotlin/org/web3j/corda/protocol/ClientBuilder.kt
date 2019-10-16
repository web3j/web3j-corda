/*
 * Copyright 2019 Web3 Labs LTD.
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

import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import javax.ws.rs.ClientErrorException
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.web3j.corda.api.AuthenticationFilter

object ClientBuilder {

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

    private class ClientErrorHandler<T>(
        private val client: T,
        private val mapper: (ClientErrorException) -> RuntimeException
    ) : InvocationHandler {

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            try {
                // Invoke the original method on the client
                return method.invoke(client, *(args ?: arrayOf())).let {
                    if (Proxy.isProxyClass(it.javaClass)) {
                        // The result is a Jersey web resource
                        // so we need to wrap it again
                        Proxy.newProxyInstance(
                            method.returnType.classLoader,
                            arrayOf(method.returnType),
                            ClientErrorHandler(it, mapper)
                        )
                    } else {
                        it
                    }
                }
            } catch (e: ClientErrorException) {
                throw mapper.invoke(e)
            } catch (e: InvocationTargetException) {
                throw if (e.targetException is ClientErrorException) {
                    mapper.invoke(e.targetException as ClientErrorException)
                } else {
                    e.targetException
                }
            }
        }
    }
}
