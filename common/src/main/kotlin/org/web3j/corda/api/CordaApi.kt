package org.web3j.corda.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.web3j.corda.model.CorDappId
import org.web3j.corda.model.CordaX500Name
import org.web3j.corda.model.FlowId
import org.web3j.corda.model.Party
import org.web3j.corda.model.SimpleNodeInfo
import org.web3j.corda.validation.HostAndPort
import org.web3j.corda.validation.X500Name
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

@Path("api/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface CordaApi {

    @GET
    @Path("cordapps")
    fun getAllCorDappIds(): List<CorDappId>

    @Path("cordapps/{corDappId}")
    fun getCorDappById(
        @PathParam("corDappId")
        corDappId: CorDappId
    ): CorDappResource

    @Path("network")
    fun getNetwork(): NetworkResource
}

interface CorDappResource {

    @GET
    @Path("flows")
    fun getAllFlowIds(): List<FlowId>

    @Path("flows/{flowId}")
    fun getFlowById(
        @PathParam("flowId")
        flowId: FlowId
    ): FlowResource
}

interface NetworkResource {

    /**
     * Retrieves all nodes.
     */
    @GET
    @Path("nodes")
    fun getAllNodes(): List<SimpleNodeInfo>

    /**
     * Retrieves by the supplied host and port.
     *
     * @param hostAndPort `host:port` for the Corda P2P of the node
     */
    @GET
    @Path("nodes")
    fun getNodesByHostAndPort(
        @Valid
        @HostAndPort
        @QueryParam("hostAndPort")
        hostAndPort: String
    ): List<SimpleNodeInfo>

    /**
     * Retrieves by the supplied X500 name.
     *
     * @param x500Name `host:port` for the Corda P2P of the node
     */
    @GET
    @Path("nodes")
    fun getNodesByX500Name(
        @Valid
        @X500Name
        @QueryParam("x500Name")
        x500Name: CordaX500Name
    ): List<SimpleNodeInfo>

    @GET
    @Path("my-node-info")
    fun getMyNodeInfo(): SimpleNodeInfo

    @GET
    @Path("notaries")
    fun getAllNotaries(): List<Party>
}

interface FlowResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun start(@Valid parameters: Any): Any
}

fun Any.toJson(): String = jacksonObjectMapper()
    .writerWithDefaultPrettyPrinter()
    .writeValueAsString(this)
