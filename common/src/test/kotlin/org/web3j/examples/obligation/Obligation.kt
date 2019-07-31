package org.web3j.examples.obligation

import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.web3j.corda.Party
import org.web3j.corda.SignedTransaction
import org.web3j.corda.protocol.Corda
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("obligation-cordapp")
interface Obligation {

    @Path("issue-obligation")
    interface Issue {

        @POST
        fun start(party: Party): SignedTransaction

        companion object {
            fun build(corda: Corda): Issue {
                val target = corda.service.client.target(corda.service.uri)
                return WebResourceFactory.newResource(Issue::class.java, target)
            }
        }
    }
}
