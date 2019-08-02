package org.web3j.corda.protocol

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

class CordaService(val uri: String) : AutoCloseable {
    internal val client: Client by lazy {

        val config = ClientConfig().apply {
            register(
                JacksonJaxbJsonProvider(
                    jacksonObjectMapper(),
                    arrayOf(Annotations.JACKSON)
                )
            )
            property(ClientProperties.READ_TIMEOUT, 15000)
            property(ClientProperties.CONNECT_TIMEOUT, 15000)
        }

        ClientBuilder.newClient(config)
    }

    override fun close() {
        client.close()
    }
}
