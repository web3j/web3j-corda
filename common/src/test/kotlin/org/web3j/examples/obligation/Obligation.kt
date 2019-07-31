package org.web3j.examples.obligation

import org.web3j.corda.Party
import org.web3j.corda.SignedTransaction
import org.web3j.corda.contracts.ContractBuilder
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("obligation-cordapp")
interface Obligation {

    @Path("issue-obligation")
    interface Issue {

        @POST
        fun start(party: Party): SignedTransaction

        class Builder : ContractBuilder<Issue>(Issue::class.java)
    }
}
