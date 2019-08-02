package org.web3j.corda.api

import org.web3j.corda.model.CorDappId
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam

interface CorDappResource {

    @GET
    fun findAll(): List<CorDappId>

    @Path("{corDappId}")
    fun findById(
        @PathParam("corDappId")
        corDappId: CorDappId
    ): CorDapp
}
