package org.web3j.corda.networkmap

import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

interface CertManApi {

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    fun generate(): String
}
