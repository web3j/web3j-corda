package org.web3j.examples.obligation

import org.web3j.corda.Party
import org.web3j.corda.SignedTransaction
import org.web3j.corda.contracts.ContractBuilder
import org.web3j.corda.protocol.Corda
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("obligation-cordapp")
interface Obligation {

    @Path("issue-obligation")
    interface Issue {

        @POST
        fun start(party: Party): SignedTransaction

        private class Builder : ContractBuilder<Issue>(Issue::class.java)

        companion object {

            @JvmStatic
            fun build(corda: Corda) = Builder().build(corda)
        }
    }
}
