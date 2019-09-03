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

import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.web3j.corda.api.AuthenticationFilter

object ProxyBuilder {

    fun <T> build(type: Class<T>, service: CordaService) = build(type, service, null)

    fun <T> build(type: Class<T>, service: CordaService, token: String?): T {
        require(type.isInterface) { "Proxy class must be an interface" }

        val target = service.client.target(service.uri)
        token?.run { target.register(AuthenticationFilter.token(token)) }

        return WebResourceFactory.newResource(type, target)
    }
}
