package org.web3j.examples.obligation

import org.web3j.corda.CordaX500Name
import org.web3j.corda.SignedTransaction
import org.web3j.corda.contracts.CorDappLifeCycle
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.ProxyBuilder
import java.io.File
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("api/rest/cordapps")
interface ObligationCorDapp {

    @Path("obligation-cordapp")
    fun getObligation(): Obligation

    interface Obligation {

        data class InitiatorParameters(
            val amount: String,
            val party: CordaX500Name,
            val anonymous: Boolean
        )

        @Path("flows/issue-obligation")
        fun getIssue(): Issue

        interface Issue {

            @POST
            @Valid
            @Produces(MediaType.APPLICATION_JSON)
            @Consumes(MediaType.APPLICATION_JSON)
            fun start(parameters: InitiatorParameters): SignedTransaction
        }
    }

    private class Builder : ProxyBuilder<ObligationCorDapp>(ObligationCorDapp::class.java)

    companion object : CorDappLifeCycle<ObligationCorDapp> {

        override fun upgrade(corda: Corda, file: File): ObligationCorDapp {
            TODO("not implemented")
        }

        override fun deploy(corda: Corda, file: File): ObligationCorDapp {
            TODO("not implemented")
        }

        @JvmStatic
        override fun load(corda: Corda) = Builder().build(corda.service)
    }
}


