package org.web3j.corda.api

import org.web3j.corda.CorDappId
import org.web3j.corda.FlowId
import org.web3j.corda.Party
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Path("api/rest")
interface CordaApi {

    @get:GET
    @get:Path("cordapps")
    val corDapps: Array<CorDappId>

    @Path("cordapps/{id}")
    fun corDappById(id: CorDappId): CorDappResource

    @get:Path("network")
    val network: NetworkResource
}

interface CorDappResource {

    @get:GET
    @get:Path("flows")
    val flows: Array<FlowId>

    @Path("flows/{id}")
    fun flowById(@PathParam("id") id: FlowId): FlowResource
}

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

interface FlowResource {

    @POST
    fun start(vararg parameters: Any): Any
}
