import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.protocol.Party
import org.web3j.corda.protocol.SignedTransaction
import javax.ws.rs.POST
import javax.ws.rs.Path

fun main() {
    val party = Party("", "")
    val service = CordaService("http://localhost:9000/")

    val corda = Corda.build(service)
    corda.network.nodes.forEach { println(it) }

    Obligation.Issue.build(corda).start(party)
}

@Path("obligation-cordapp")
interface Obligation {

    interface Issue {

        @POST
        @Path("issue-obligation")
        fun start(party: Party): SignedTransaction

        companion object {
            fun build(corda: Corda): Issue {
                val target = corda.service.client.target(corda.service.uri)
                return WebResourceFactory.newResource(Issue::class.java, target)
            }
        }
    }
}