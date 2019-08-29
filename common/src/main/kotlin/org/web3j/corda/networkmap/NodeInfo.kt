package org.web3j.corda.networkmap

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

interface NodeInfo {

    /**
     * Retrieve a signed NodeInfo as specified in the network map object.
     */
    @GET
    @Path("{hash}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun findById(@PathParam("hash") hash: String): ByteArray
}
