/*
 * Copyright 2019 Web3 Labs LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.corda.examples.obligation

import assertk.assertThat
import assertk.assertions.isEqualTo
import java.io.File
import java.math.BigDecimal
import org.junit.jupiter.api.Test
import org.web3j.corda.examples.obligation.flows.IssueObligation_InitiatorPayload
import org.web3j.corda.finance.flows.CashIssueAndPaymentFlowPayload
import org.web3j.corda.finance.flows.CashIssueFlowPayload
import org.web3j.corda.finance.workflows.api.CordaFinanceWorkflows
import org.web3j.corda.model.AmountCurrency
import org.web3j.corda.model.core.utilities.OpaqueBytes
import org.web3j.corda.network.network
import org.web3j.corda.network.nodes
import org.web3j.corda.network.notary
import org.web3j.corda.network.party
import org.web3j.corda.obligation.api.Obligation

class ObligationKotlinTest {

    private val network = network {
        baseDir = File(javaClass.classLoader.getResource("cordapps")!!.file)
        nodes {
            notary {
                name = "O=Notary, L=London, C=GB"
            }
            party {
                name = "O=PartyA, L=London, C=GB"
            }
            party {
                name = "O=PartyB, L=New York, C=US"
            }
        }
    }

    @Test
    fun `issue obligation`() {

        val partyB = network.nodes[0].api.network.nodes
            .findByX500Name("O=PartyB,L=New York,C=US")[0].legalIdentities[0]

        Obligation.load(network.nodes[0].api.service).flows.issueObligationInitiator.start(
            IssueObligation_InitiatorPayload(
                AmountCurrency(100, BigDecimal.ONE, "GBP"),
                partyB,
                false
            )
        ).apply {
            assertThat(coreTransaction!!.outputs[0].data!!.participants?.first()?.owningKey).isEqualTo(partyB.owningKey)
        }
    }

    @Test
    fun `cash issue flow`() {

        val notary = network.nodes[0].api.network.notaries.findAll().first()
        val partyA = network.nodes[0].api.network.nodes.self

        val partyB = network.nodes[0].api.network.nodes
            .findByX500Name("O=PartyB,L=New York,C=US")[0].legalIdentities[0]

        CordaFinanceWorkflows.load(network.nodes[0].api.service).flows.cashIssueFlow.start(
            CashIssueFlowPayload(
                amount = AmountCurrency(100, BigDecimal.valueOf(0.01), "GBP"),
                issuerBankPartyRef = OpaqueBytes("736F6D654279746573"),
                notary = notary
            )
        ).apply {
            assertThat(recipient!!.owningKey).isEqualTo(partyA.legalIdentities.first().owningKey)
        }

        CordaFinanceWorkflows.load(network.nodes[0].api.service).flows.cashIssueAndPaymentFlow.start(
            CashIssueAndPaymentFlowPayload(
                amount = AmountCurrency(100, BigDecimal.valueOf(0.01), "GBP"),
                issueRef = OpaqueBytes("736F6D654279746573"),
                recipient = partyB,
                anonymous = false,
                notary = notary
            )
        ).apply {
            assertThat(recipient!!.owningKey).isEqualTo(partyB.owningKey)
        }
    }
}
