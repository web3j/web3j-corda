/*
 * Copyright 2019 Web3 Labs Ltd.
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
package org.web3j.corda.examples.finance

import assertk.assertThat
import assertk.assertions.isEqualTo
import java.math.BigDecimal
import org.junit.jupiter.api.Test
import org.web3j.corda.examples.network
import org.web3j.corda.finance.flows.CashIssueAndPaymentFlowPayload
import org.web3j.corda.finance.flows.CashIssueFlowPayload
import org.web3j.corda.finance.workflows.api.CordaFinanceWorkflows
import org.web3j.corda.model.AmountCurrency
import org.web3j.corda.model.core.utilities.OpaqueBytes

class CordaFinanceKotlinTest {

    @Test
    fun `cash issue flow`() {

        val notary = network.parties[0].corda.api.network.notaries.findAll().first()
        val partyA = network.parties[0].corda.api.network.nodes.self

        CordaFinanceWorkflows.load(network.parties[0].corda.service).flows.cashIssueFlow.start(
            CashIssueFlowPayload(
                amount = AmountCurrency(100, BigDecimal.valueOf(0.01), "GBP"),
                issuerBankPartyRef = OpaqueBytes("736F6D654279746573"),
                notary = notary
            )
        ).apply {
            assertThat(recipient!!.owningKey).isEqualTo(partyA.legalIdentities.first().owningKey)
        }
    }

    @Test
    fun `cash issue and payment flow`() {
        val notary = network.parties[0].corda.api.network.notaries.findAll().first()
        val partyB = network.parties[0].corda.api.network.nodes
            .findByX500Name("O=PartyB,L=New York,C=US")[0].legalIdentities[0]

        CordaFinanceWorkflows.load(network.parties[0].corda.service).flows.cashIssueAndPaymentFlow.start(
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
