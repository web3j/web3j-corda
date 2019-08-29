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

import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.server.validation.ValidationFeature
import org.web3j.corda.util.mapper
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

class CordaService(
    val uri: String,
    readTimeout: Long = DEFAULT_READ_TIMEOUT,
    connectTimeout: Long = DEFAULT_CONNECT_TIMEOUT
) : AutoCloseable {

    internal val client: Client by lazy {

        val config = ClientConfig().apply {
            register(LoggingFeature())
            register(ValidationFeature())
            register(JacksonJaxbJsonProvider(mapper, arrayOf(Annotations.JACKSON)))
            property(ClientProperties.READ_TIMEOUT, readTimeout)
            property(ClientProperties.CONNECT_TIMEOUT, connectTimeout)
        }

        ClientBuilder.newClient(config)
    }

    override fun close() {
        client.close()
    }

    companion object {
        const val DEFAULT_READ_TIMEOUT: Long = 5000
        const val DEFAULT_CONNECT_TIMEOUT: Long = 5000
    }
}
