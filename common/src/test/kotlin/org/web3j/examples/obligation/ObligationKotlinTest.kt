package org.web3j.examples.obligation

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.web3j.corda.SignedTransaction
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService

class ObligationKotlinTest {

    @Test
    fun `issue obligation`() {
        val party = corda.getNetwork().getAllNodes()[2].legalIdentities[0]

        // 1. Normal version, not type safe
        val signedTx = corda
            .getCorDappById("obligation-cordapp")
            .getFlowById("issue-obligation")
            .start(party) as SignedTransaction

        // 2. web3j generated version, 100% type safe
        Obligation.build(corda).getIssue().start("$1", party.name, false)
    }

    companion object {

        @JvmStatic
        private lateinit var corda: Corda

        @JvmStatic
        private lateinit var service: CordaService

        @BeforeAll
        @JvmStatic
        @Throws(Exception::class)
        fun setUpClass() {
            service = CordaService("http://localhost:9000/")
            corda = Corda.build(service)
        }

        @AfterAll
        internal fun tearDownClass() {
            service.close()
        }
    }
}
