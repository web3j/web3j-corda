package org.web3j.corda.networkmap

import javax.validation.constraints.NotNull
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM

interface NetworkMap {

    /**
     * Retrieve the current signed network map object.
     * The entire object is signed with the network map certificate which is also attached.
     */
    @GET
    @Produces(APPLICATION_OCTET_STREAM)
    fun get(): ByteArray

    /**
     * For the node to upload its signed NodeInfo object to the network map.
     */
    @POST
    @Path("publish")
    @Consumes(APPLICATION_OCTET_STREAM)
    fun publish(@NotNull nodeInfo: ByteArray)

    /**
     * For the node operator to acknowledge network map that new parameters were accepted for future update.
     */
    @POST
    @Path("ack-parameters")
    @Consumes(APPLICATION_OCTET_STREAM)
    fun ackParameters(@NotNull signedSecureHash: ByteArray)

    @get:Path("node-info")
    val nodeInfo: NodeInfo

    @get:Path("network-parameters")
    val networkParameters: NetworkParameters


    /**
     * Retrieve this network-map's truststore.
     */
    @get:GET
    @get:Produces(APPLICATION_OCTET_STREAM)
    @get:Consumes(APPLICATION_OCTET_STREAM)
    val truststore: ByteArray
}