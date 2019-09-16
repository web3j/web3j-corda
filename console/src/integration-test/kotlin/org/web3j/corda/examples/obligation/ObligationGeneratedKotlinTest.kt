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
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.corda.network.CordaNetwork.Companion.network
import org.web3j.corda.obligation.api.Obligation
import org.web3j.corda.obligation.model.IssueObligationInitiatorParameters
import java.io.File

@Testcontainers
class ObligationGeneratedKotlinTest {

    @Test
    fun `issue obligation`() {
        val party = network.nodes["PartyA"].api.network.nodes.findAll()[2].legalIdentities[0]
        val parameters = IssueObligationInitiatorParameters("$1", party.name!!, false)

        val signedTx = Obligation.load(network.nodes["PartyA"].api).flows.issue.start(parameters)
        assertThat(signedTx.coreTransaction.outputs[0].data.participants.first().owningKey).isEqualTo(party.owningKey)
    }

    companion object {

        private val network = network {
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
