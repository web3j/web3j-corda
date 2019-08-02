package org.web3j.corda.api

import org.web3j.corda.model.FlowId
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam

interface FlowResource {

    @GET
    fun findAll(): List<FlowId>

    @Path("{flowId}")
    fun findById(
        @PathParam("flowId")
        flowId: FlowId
    ): StartableFlow
}
