package org.web3j.corda.networkmap

import org.web3j.corda.model.LoginRequest
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

interface AdminApi {

    /**
     * Obtains a valid token.
     */
    @POST
    @Path("login")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    fun login(request: LoginRequest): String

}
