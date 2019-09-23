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
import org.junit.jupiter.api.Test
import org.web3j.corda.model.AmountCurrency
import org.web3j.corda.network.CordaNetwork
import org.web3j.corda.network.network
import org.web3j.corda.network.node
import org.web3j.corda.network.nodes
import org.web3j.corda.obligation.api.Obligation
import org.web3j.corda.obligation.model.IssueObligationInitiatorPayload
import java.io.File

class ObligationGeneratedKotlinTest {

    @Test
    fun `issue obligation`() {
        val partyB = network.nodes["PartyA"].api.network.nodes.findAll()[2].legalIdentities[0]

        val parameters = IssueObligationInitiatorPayload(
            AmountCurrency(100, 2, "GBP"),
            partyB,
            false
        )

        Obligation.load(network.nodes["PartyA"].api)
            .flows.issueObligationInitiator.start(parameters).apply {
            assertThat(coreTransaction.outputs[0].data.participants.first().owningKey)
                .isEqualTo(partyB.owningKey)
        }
    }

    companion object {
        private val network = CordaNetwork.network {
            baseDir = File("/Users/xavier/Development/Projects/Web3Labs/web3j-corda-samples/kotlin-source")
            nodes {
                node {
                    name = "Notary"
                    location = "London"
                    country = "GB"
                    isNotary = true
                }
                node {
                    name = "PartyA"
                    location = "Tokyo"
                    country = "JP"
                }
                node {
                    name = "PartyB"
                    location = "New York"
                    country = "US"
                }
            }
        }
    }
}
