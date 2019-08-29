package org.web3j.corda.networkmap

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

interface Certificate {

    /**
     * Receives a certificate signing request.
     */
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    fun create(pkcS10CertificationRequest: ByteArray)

    /**
     * Retrieve the certificate chain as a zipped binary block.
     */
    @GET
    @Path("{certificateId}")
    @Produces(MediaType.TEXT_PLAIN)
    fun findById(@PathParam("certificateId") id: String): String
}
