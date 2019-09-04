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
import assertk.assertions.hasSize
import assertk.assertions.isDataClassEqualTo
import assertk.assertions.isEmpty
import io.bluebank.braid.server.Braid
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.corda.model.NetworkHostAndPort
import org.web3j.corda.model.Party
import org.web3j.corda.model.SimpleNodeInfo
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.testcontainers.DockerBasedIntegrationTest

/**
 * TODO Implement tests for all routes and error cases.
 */
@Testcontainers
class CordaIntegrationTest : DockerBasedIntegrationTest() {

    @Test
    @Disabled("https://gitlab.com/bluebank/braid/issues/111")
    internal fun `corDapps resource`() {

        corda.corDapps.findAll().apply {
            assertThat(this).isEmpty()
        }

        corda.corDapps.findById("xyz").apply {
            // TODO Assertion null or exception
        }
    }

    @Test
    internal fun `network resource`() {

        corda.network.nodes.self.apply {
            assertThat(legalIdentities).hasSize(1)
            assertThat(legalIdentities.first()).isDataClassEqualTo(party)
        }

        corda.network.nodes.findAll().apply {
            assertThat(this).isDataClassEqualTo(notary)
        }

        corda.network.nodes.findByX500Name(party.name!!).apply {
            assertThat(this).isDataClassEqualTo(notary)
        }

        corda.network.nodes.findByHostAndPort("localhost:10005").apply {
            assertThat(this).isDataClassEqualTo(notary)
        }

        corda.network.notaries.findAll().apply {
            assertThat(this).hasSize(1)
            assertThat(first()).isDataClassEqualTo(party)
        }
    }

    companion object {

        private lateinit var corda: Corda

        private val party = Party(
            name = "O=Notary, L=London, C=GB",
            owningKey = "GfHq2tTVk9z4eXgyQKmUDm9Hyk7bB8yh6bMXvhmaikGFxUDrHhFnJhNiqN5Z"
        )

        private val notary = SimpleNodeInfo(
            addresses = listOf(NetworkHostAndPort("localhost", 10005)),
            legalIdentities = listOf(party)
        )

        @BeforeAll
        @JvmStatic
        fun setUp() {
            val container = createNodeContainer(
                "Notary",
                "London",
                "GB",
                10005,
                10006,
                10007,
                true
            ).apply {
                start()
            }

            Braid().withPort(9000)
                .withUserName("user1")
                .withPassword("test")
                .withNodeAddress("localhost:${container.getMappedPort(10006)}")
                .startServer()

            corda = Corda.build(CordaService("http://localhost:9000"))
            Thread.sleep(10000)
        }
    }
}
