package org.web3j.corda.protocol

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

class CordaService(val uri: String) : AutoCloseable {
    internal val client: Client by lazy {
        ClientBuilder.newClient()
    }

    override fun close() {
        client.close()
    }
}
