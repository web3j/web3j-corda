package org.web3j.examples.obligation

import org.web3j.corda.CordaX500Name
import org.web3j.corda.SignedTransaction
import org.web3j.corda.contracts.ContractBuilder
import org.web3j.corda.protocol.Corda
import org.web3j.corda.validation.X500Name
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("api/rest/cordapps/obligation-cordapp")
interface Obligation {

    @Path("flows/issue-obligation")
    fun getIssue(): Issue

    interface Issue {

        @POST
        @Valid
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        fun start(amount: String, @X500Name party: CordaX500Name, anonymous: Boolean): SignedTransaction
    }

    private class Builder : ContractBuilder<Obligation>(Obligation::class.java)

    companion object {

        @JvmStatic
        fun build(corda: Corda) = Builder().build(corda)
    }
}
