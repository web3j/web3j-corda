package org.web3j.corda.protocol

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.glassfish.jersey.client.proxy.WebResourceFactory
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

@Path("api/rest")
interface Corda {

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

        @Path("{id}")
        operator fun get(@PathParam("id") id: CorDappId): CorDapp
    }

    interface CorDapp {
        val corDappId: CorDappId

        @get:Path("flows")
        val flows: FlowsResource
    }

    interface FlowsResource {

        @GET
        @Path("{id}")
        operator fun get(@PathParam("id") id: FlowId): Flow
    }

    interface Flow {
        val corDappId: CorDappId
        val flowId: FlowId
    }

    companion object {
        fun build(service: CordaService): Corda {
            val target = service.client.target(service.target)
            return WebResourceFactory.newResource(Corda::class.java, target)
        }
    }
}

class CordaService(val target: String) : AutoCloseable {
    internal val client: Client by lazy {
        ClientBuilder.newClient()
    }

    override fun close() {
        client.close()
    }
}

abstract class BaseFlow protected constructor(
    override val corDappId: CorDappId,
    override val flowId: FlowId,
    private val corda: Corda
) : Corda.Flow {

    protected open fun start(vararg params: Any): Any {
        // TODO Type conversion and invocation
        return corda.corDapps[corDappId].flows[flowId]
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