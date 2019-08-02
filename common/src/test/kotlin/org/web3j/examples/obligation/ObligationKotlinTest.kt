package org.web3j.examples.obligation

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.web3j.corda.model.SignedTransaction
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.examples.obligation.Obligation.Issue.InitiatorParameters

class ObligationKotlinTest {

    @Test
    fun `issue obligation`() {
        val party = corda.getNetwork().getAllNodes()[2].legalIdentities[0]
        val parameters = InitiatorParameters("$1", party.name, false)

        // 1. Normal version, not type safe
        var signedTx = corda
            .getCorDappById("obligation-cordapp")
            .getFlowById("issue-obligation")
            .start(parameters) as SignedTransaction

        // 2. web3j generated version, 100% type safe
        signedTx = Obligation.load(corda)
            .getIssue()
            .start(parameters)
    }

    @Test
    internal fun `validate party name`() {
        val parameters = InitiatorParameters("$1", "PartyX", false)

        val signedTx = Obligation.load(corda)
            .getIssue()
            .start(parameters)
    }

    companion object {

        @JvmStatic
        private lateinit var corda: Corda

        @JvmStatic
        private lateinit var service: CordaService

        @BeforeAll
        @JvmStatic
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
