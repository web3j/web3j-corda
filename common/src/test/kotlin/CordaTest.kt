import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.web3j.corda.protocol.*
import javax.ws.rs.POST
import javax.ws.rs.Path

fun main() {
    val party = Party("", "")
    val service = CordaService("http://localhost:9000/")

    val corda = Corda.build(service)
    corda.network.nodes.forEach { println(it) }
    corda.corDapps[""].flows[""].start()

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
