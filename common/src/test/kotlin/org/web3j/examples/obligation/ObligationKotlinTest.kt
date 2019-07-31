package org.web3j.examples.obligation

import org.junit.jupiter.api.Test
import org.web3j.corda.api.toJson
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService

class ObligationKotlinTest {

    @Test
    fun `issue obligation`() {
        val service = CordaService("http://localhost:9000/")

        val corda = Corda.build(service)
        corda.getNetwork().getAllNodes().forEach { println(it.toJson()) }

        // 1. Normal version, not type safe
//        val signedTx = corda
//            .getCorDappById("obligation-cordapp")
//            .getFlowById("issue-obligation")
//            .start(party) as SignedTransaction

        // 2. web3j generated version, 100% type safe
        val party = corda.getNetwork().getAllNodes()[2].legalIdentities[0]
        Obligation.build(corda).getIssue().start("$1", party.name, false)

        service.close()
    }
}
