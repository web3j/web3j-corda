package org.web3j.corda.api

import org.web3j.corda.model.CordaX500Name
import org.web3j.corda.model.Party
import org.web3j.corda.validation.X500Name
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.QueryParam

interface NotaryResource {

    /**
     * Retrieves all notaries.
     */
    @GET
    fun findAll(): List<Party>

    /**
     * Retrieves by the supplied X500 name, e.g. `O=PartyB, L=New York, C=US`.
     *
     * @param x500Name the X500 name for the node.
     */
    @GET
    fun findByX500Name(
        @Valid
        @X500Name
        @QueryParam("x500-name")
        x500Name: CordaX500Name
    ): List<Party>
}
