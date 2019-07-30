import org.web3j.corda.protocol.*

fun main() {
    val party = Party("", "")
    val client = CordaService("http://localhost:9000/")

    val corda = Corda.build(client)
    corda.network.nodes.forEach { println(it) }

    IssueObligation.build(corda).start(party)
}

// TODO Auto generate contract wrapper 
class IssueObligation private constructor(corda: Corda) : BaseFlow(
    "obligation-cordapp",
    "issue-obligation",
    corda
) {
    fun start(party: Party): SignedTransaction {
        return super.start(party) as SignedTransaction
    }

    companion object {
        fun build(corda: Corda): IssueObligation {
            return IssueObligation(corda)
        }
    }
}
