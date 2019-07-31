import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.web3j.corda.protocol.*
import javax.ws.rs.POST
import javax.ws.rs.Path

fun main() {
    val party = Party("", "")
    val service = CordaService("http://localhost:9000/")

    val corda = Corda.build(service)
    corda.network.nodes.forEach { println(it) }

    // 1. Normal version, not type safe  
    var signedTx: SignedTransaction = corda.corDapps["obligation-cordapp"]
        .flows.start("issue-obligation", party) as SignedTransaction

    // 2. Extension version, not type safe but nicer
    signedTx = corda.corDapps["obligation-cordapp"]
        .flows["issue-obligation"].start(party)

    // 3. web3j generated version, 100% type safe 
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
