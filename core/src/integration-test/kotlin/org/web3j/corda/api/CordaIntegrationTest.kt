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
package org.web3j.corda.api

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.web3j.corda.network.CordaNetwork
import org.web3j.corda.network.network
import org.web3j.corda.network.node
import org.web3j.corda.network.nodes
import org.web3j.corda.protocol.Corda

class CordaIntegrationTest {

    @Test
    internal fun `corDapps resource`() {
        assertThat(corda.corDapps.findAll()).containsOnly("corda-core")

        corda.corDapps.findById("corda-core").apply {
            assertThat(flows.findAll()).containsOnly(
                "net.corda.core.flows.ContractUpgradeFlow\$Authorise",
                "net.corda.core.flows.ContractUpgradeFlow\$Deauthorise"
            )
        }
    }

    @Test
    internal fun `network resource`() {
        corda.network.nodes.self.apply {
            assertThat(legalIdentities).hasSize(1)
            assertThat(legalIdentities.first().name).isEqualTo(party)
        }
        corda.network.nodes.findAll().flatMap { node ->
            node.legalIdentities.map { party -> party.name }
        }.apply {
            assertThat(this).containsOnly(party, notary)
        }
        corda.network.nodes.findByX500Name(party).apply {
            assertThat(this).hasSize(1)
            assertThat(first().legalIdentities.map { it.name }).containsOnly(party)
        }
        corda.network.nodes.findByHostAndPort("localhost:${network.nodes[party].p2pPort}").apply {
            assertThat(this).hasSize(1)
            assertThat(first().legalIdentities.map { it.name }).containsOnly(party)
        }
        corda.network.notaries.findAll().apply {
            assertThat(this).hasSize(1)
            assertThat(first().name).isEqualTo(notary)
        }
    }

    companion object {

        private const val party = "O=Party, L=New York, C=US"
        private const val notary = "O=Notary, L=London, C=GB"

        private val corda: Corda by lazy {
            network.nodes[party].api
        }

        private val network = CordaNetwork.network {
            nodes {
                node {
                    name = notary
                    isNotary = true
                }
                node {
                    name = party
                }
            }
        }
    }
}
