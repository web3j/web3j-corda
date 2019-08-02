package org.web3j.examples.obligation

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.web3j.corda.model.SignedTransaction
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.util.convert
import org.web3j.examples.obligation.Obligation.ObligationFlowResource.Issue.InitiatorParameters

class ObligationKotlinTest {

    @Test
    fun `issue obligation`() {
        val party = corda.network.nodes.findAll()[2].legalIdentities[0]
        val parameters = InitiatorParameters("$1", party.name!!, false)

        // 1. Normal version, not type-safe
        val issue = corda
            .corDapps.findById("obligation-cordapp")
            .flows.findById("issue-obligation")

        // Potential runtime exception!
        val signedTxAny = issue.start(parameters)
        issue.progressTracker.steps.current.label

        // Potential runtime exception!
        var signedTx = convert<SignedTransaction>(signedTxAny)
        assertThat(signedTx.coreTransaction.outputs[0]?.data?.lender?.name).isEqualTo(party.name)

        // 2. web3j generated version, 100% type-safe
        val issueFlow = Obligation.load(corda).flows.issue
        signedTx = issueFlow.start(parameters)

        assertThat(signedTx.coreTransaction.outputs[0]?.data?.lender?.name).isEqualTo(party.name)
        issueFlow.progressTracker.steps.current.label
    }

    @Test
    internal fun `validate party name`() {
        val parameters = InitiatorParameters("$1", "PartyX", false)
        val signedTx = Obligation.load(corda).flows.issue.start(parameters)
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
