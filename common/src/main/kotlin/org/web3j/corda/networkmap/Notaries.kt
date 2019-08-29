package org.web3j.corda.networkmap

import org.web3j.corda.model.NotaryType
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.MediaType

interface Notaries {

    /**
     * Upload a signed NodeInfo object to the network map.
     */
    @POST
    @Path("{notaryType}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    fun create(@PathParam("notaryType") type: NotaryType, nodeInfo: ByteArray)

    /**
     * Delete a notary with the node key.
     */
    @DELETE
    @Path("{notaryType}")
    @Consumes(MediaType.TEXT_PLAIN)
    fun delete(@PathParam("notaryType") type: NotaryType, nodeKey: String)
}
