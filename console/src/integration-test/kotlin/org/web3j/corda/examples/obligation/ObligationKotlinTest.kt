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
package org.web3j.corda.examples.obligation

import assertk.assertThat
import assertk.assertions.isEqualTo
import java.math.BigDecimal
import org.junit.jupiter.api.Test
import org.web3j.corda.examples.network
import org.web3j.corda.examples.obligation.flows.IssueObligation_InitiatorPayload
import org.web3j.corda.model.AmountCurrency
import org.web3j.corda.obligation.api.Obligation

class ObligationKotlinTest {

    @Test
    fun `issue obligation`() {

        val nodeA = network.parties[0]

        val partyB = network.parties[0].corda.api.network.nodes
            .findByX500Name("O=PartyB,L=New York,C=US")[0].legalIdentities[0]

        Obligation.load(network.parties[0].corda.service).flows.issueObligationInitiator.start(
            IssueObligation_InitiatorPayload(
                AmountCurrency(100, BigDecimal.ONE, "GBP"),
                partyB,
                false
            )
        ).apply {
            assertThat(coreTransaction!!.outputs[0].data!!.participants?.first()?.owningKey).isEqualTo(partyB.owningKey)
            val vaultQueryRes = nodeA.corda.api.vault.queryBy.contractStateType("net.corda.examples.obligation.Obligation")
            val nodeState = vaultQueryRes.states[0].state?.data
            assertThat(nodeState?.participants?.first()?.owningKey).isEqualTo(partyB.owningKey)
        }
    }
}
