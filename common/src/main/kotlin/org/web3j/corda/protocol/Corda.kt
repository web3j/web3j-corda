package org.web3j.corda.protocol

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.glassfish.jersey.client.proxy.WebResourceFactory
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

@Path("api/rest")
interface CordaApi {

    @get:Path("cordapps")
    val corDapps: CorDappsResource

    @get:Path("network")
    val network: NetworkResource

    interface NetworkResource {

        @get:GET
        @get:Path("nodes")
        val nodes: Array<Party>

        @get:GET
        @get:Path("notaries")
        val notaries: Array<Party>

        @get:GET
        @get:Path("my-node-info")
        val myNodeInfo: Party
    }

    interface Network {
        interface NodeInfo {
            val commonName: String
        }
    }

    interface CorDappsResource {

        @GET
        fun list(): Array<CorDapp>

        @GET
        @Path("{id}")
        operator fun get(@PathParam("id") id: CorDappId): CorDapp
    }

    interface CorDapp {

        @get:Path("flows")
        val flows: FlowsResource
    }

    interface FlowsResource {

        @GET
        fun list(): Array<Flow>

        @GET
        @Path("{id}")
        operator fun get(@PathParam("id") id: FlowId): Flow

        @POST
        @Path("{id}")
        fun start(@PathParam("id") id: FlowId, vararg parameters: Any): Any
    }

    interface Flow
}

inline fun <reified T> CordaApi.Flow.start(vararg parameters: Any): T {
    TODO("Improved syntax for starting a flow")
}

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

class CordaService(val uri: String) : AutoCloseable {
    internal val client: Client by lazy {
        ClientBuilder.newClient()
    }

    override fun close() {
        client.close()
    }
}

data class SignedTransaction(
    val txBits: SerializedBytesCoreTransaction
)

data class SerializedBytesCoreTransaction(
    val offset: Int,
    val size: Int
)

class Party @JsonCreator constructor(
    @field:JsonProperty("owningKey") val owningKey: PublicKey,
    @field:JsonProperty("name") val name: CordaX500Name
)

typealias CordaX500Name = String
typealias PublicKey = String
typealias CorDappId = String
typealias FlowId = String