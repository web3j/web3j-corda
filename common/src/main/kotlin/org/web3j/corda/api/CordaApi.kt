package org.web3j.corda.api

import org.web3j.corda.CorDappId
import org.web3j.corda.FlowId
import org.web3j.corda.Party
import org.web3j.corda.constraints.HostAndPort
import org.web3j.corda.constraints.X500Name
import javax.ws.rs.*

@Path("api/rest")
interface CordaApi {

    @GET
    @Path("cordapps")
    fun getAllCorDapps(): List<CorDappId>

    @Path("cordapps/{id}")
    fun getCorDappById(id: CorDappId): CorDappResource

    @Path("network")
    fun getNetwork(): NetworkResource
}

interface CorDappResource {

    @GET
    @Path("flows")
    fun getAllFlows(): List<FlowId>

    @Path("flows/{id}")
    fun getFlowById(@PathParam("id") id: FlowId): FlowResource
}

interface NetworkResource {

    /**
     * Retrieves all nodes.
     */
    @GET
    @Path("nodes")
    fun getAllNodes(): List<Party>

    /**
     * Retrieves by the supplied host and port.
     *
     * @param hostAndPort `host:port` for the Corda P2P of the node
     */
    @GET
    @Path("nodes")
    fun getNodesByHostAndPort(@QueryParam("hostAndPort") @HostAndPort hostAndPort: String): List<Party>

    /**
     * Retrieves by the supplied X500 name.
     *
     * @param x500Name `host:port` for the Corda P2P of the node
     */
    @GET
    @Path("nodes")
    fun getNodesByX500Name(@QueryParam("x500Name") @X500Name x500Name: String): List<Party>

    @GET
    @Path("notaries")
    fun getAllNotaries(): List<Party>

    @GET
    @Path("my-node-info")
    fun getMyNodeInfo(): Party
}

interface FlowResource {

    @POST
    fun start(vararg parameters: Any): Any
}
