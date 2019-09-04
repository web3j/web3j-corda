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
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.bluebank.braid.server.Braid
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.testcontainers.DockerBasedIntegrationTest

/**
 * TODO Implement tests.
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
            assertThat(legalIdentities.first().name).isEqualTo("O=Notary, L=London, C=GB")
        }

        corda.network.nodes.findAll().apply {
            assertThat(this).isEmpty()
        }

        corda.network.nodes.findByX500Name("").apply {
            assertThat(this).isEmpty()
        }

        corda.network.nodes.findByHostAndPort("").apply {
            assertThat(this).isEmpty()
        }

        corda.network.notaries.findAll().apply {
            assertThat(this).hasSize(1)
            assertThat(first().name).isEqualTo("Notary")
        }
    }

    companion object {

        private lateinit var corda: Corda

        @BeforeAll
        @JvmStatic
        fun setUp() {
            val notary = createNodeContainer(
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
                .withNodeAddress("localhost:${notary.getMappedPort(10006)}")
                .startServer()

            corda = Corda.build(CordaService("http://localhost:9000"))
            Thread.sleep(10000)
        }
    }
}
