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
import generated.net.corda.core.flows.ContractUpgradeFlow_AuthorisePayload
import generated.net.corda.examples.obligation.flows.IssueObligation_InitiatorPayload
import generated.net.corda.finance.flows.CashIssueFlowPayload
import org.junit.jupiter.api.Test
import org.web3j.corda.core.api.CordaCore
import org.web3j.corda.finance.workflows.api.CordaFinanceWorkflows
import org.web3j.corda.model.AmountCurrency
import org.web3j.corda.model.core.contracts.StateAndRef_Object
import org.web3j.corda.model.core.contracts.StateRef
import org.web3j.corda.model.core.contracts.TransactionState_Object
import org.web3j.corda.network.CordaNetwork
import org.web3j.corda.network.network
import org.web3j.corda.network.node
import org.web3j.corda.network.nodes
import org.web3j.corda.obligation.api.Obligation
import java.io.File
import java.math.BigDecimal

class ObligationKotlinTest {

    @Test
    fun `issue obligation`() {
        val partyB = network.nodes["PartyA"].api.network.nodes
            .findByX500Name("O=PartyB,L=New York,C=US")[0].legalIdentities[0]

        Obligation.load(network.nodes["PartyA"].api).flows.issueObligationInitiator.start(
            IssueObligation_InitiatorPayload(
                AmountCurrency(100, BigDecimal.ONE, "GBP"),
                partyB,
                false
            )
        ).apply {
            assertThat(coreTransaction!!.outputs[0].data!!.participants.first().owningKey).isEqualTo(partyB.owningKey)
        }
    }

    @Test
    fun `authorise contract upgrade flow`() {
        val notary = network.nodes["PartyA"].api.network.notaries.findAll().first()

        CordaCore.load(network.nodes["PartyA"].api).flows.contractUpgradeFlowAuthorise.start(
            ContractUpgradeFlow_AuthorisePayload(
                StateAndRef_Object(
                    TransactionState_Object(
                        data = "authorise payload data",
                        contract = "My Contract",
                        encumbrance = 1,
                        notary = notary
                    ),
                    StateRef("hash", 1)
                )
            )
        )
    }

    @Test
    fun `cash issue flow`() {

        val notary = network.nodes["PartyA"].api.network.notaries.findAll().first()
        val partyB = network.nodes["PartyA"].api.network.nodes
            .findByX500Name("O=PartyB,L=New York,C=US")[0].legalIdentities[0]

        CordaFinanceWorkflows.load(network.nodes["PartyA"].api).flows.cashIssueFlow.start(
            CashIssueFlowPayload(
                amount = AmountCurrency(500, BigDecimal.ONE, "ETH"),
                issuerBankPartyRef = "O=PartyB,L=New York,C=US",
                notary = notary
            )
        ).apply {
            assertThat(recipient!!.owningKey).isEqualTo(partyB.owningKey)
        }
    }

    companion object {
        private val network = CordaNetwork.network {
            baseDir = File(javaClass.classLoader.getResource("cordapps")!!.file)
            nodes {
                node {
                    name = "O=Notary,L=London,C=GB"
                    isNotary = true
                }
                node {
                    name = "O=PartyA,L=London,C=GB"
                }
                node {
                    name = "O=PartyB,L=New York,C=US"
                }
            }
        }
    }
}
