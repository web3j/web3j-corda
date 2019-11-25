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
package org.web3j.corda.api

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.web3j.corda.network.network
import org.web3j.corda.network.nodes
import org.web3j.corda.network.notary
import org.web3j.corda.network.party

class CordaIntegrationTest {

    private val network = network {
        nodes {
            notary { name = NOTARY }
            party { name = PARTY }
        }
    }

    @Test
    fun `corDapps resource`() {
        with(network.parties[0].corda.api) {
            assertThat(corDapps.findAll()).containsOnly("braid-server")
            assertThat(corDapps.findById("braid-server").flows.findAll()).isEmpty()
        }
    }

    @Test
    fun `network resource`() {
        with(network.parties[0].corda.api) {
            network.nodes.self.apply {
                assertThat(legalIdentities).hasSize(1)
                assertThat(legalIdentities.first().name).isEqualTo(PARTY)
            }
            network.nodes.findAll().flatMap { node ->
                node.legalIdentities.map { party -> party.name }
            }.apply {
                assertThat(this).containsOnly(PARTY, NOTARY)
            }
            network.nodes.findByX500Name(PARTY).apply {
                assertThat(this).hasSize(1)
                assertThat(first().legalIdentities.map { it.name }).containsOnly(PARTY)
            }
            val p2pPort = this@CordaIntegrationTest.network.parties[0].p2pPort
            network.nodes.findByHostAndPort("party-new-york-us:$p2pPort").apply {
                assertThat(this).hasSize(1)
                assertThat(first().legalIdentities.map { it.name }).containsOnly(PARTY)
            }
            network.notaries.findAll().apply {
                assertThat(this).hasSize(1)
                assertThat(first().name).isEqualTo(NOTARY)
            }
        }
    }

    companion object {
        private const val PARTY = "O=Party, L=New York, C=US"
        private const val NOTARY = "O=Notary, L=London, C=GB"
    }
}
