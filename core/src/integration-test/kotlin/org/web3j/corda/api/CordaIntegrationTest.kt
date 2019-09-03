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

import io.bluebank.braid.server.Braid
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.testcontainers.DockerBasedIntegrationTest

/**
 * TODO Implement tests
 */
@Testcontainers
class CordaIntegrationTest : DockerBasedIntegrationTest() {

    @Test
    internal fun `corDapps resource`() {

        corda.corDapps.findAll().forEach {
            // Assertions
        }

        corda.corDapps.findById("").apply {
            // Assertions
        }
    }

    @Test
    internal fun `network resource`() {

        corda.network.nodes.self.apply {
            // Assertions
        }

        corda.network.nodes.findAll().apply {
            // Assertions
        }

        corda.network.nodes.findByX500Name("").apply {
            // Assertions
        }

        corda.network.nodes.findByHostAndPort("").apply {
            // Assertions
        }
    }

    companion object {

        private lateinit var corda: Corda

        @BeforeAll
        internal fun setUp() {
            val notary = createNodeContainer("Notary", "London", "GB", 10005, 10006, 10007, true)
            notary.start()

            Braid().withPort(9000)
                .withUserName("user1")
                .withPassword("test")
                .withNodeAddress("localhost:${notary.getMappedPort(10006)}")
                .startServer()

            corda = Corda.build(CordaService("localhost:9000"))
        }
    }
}
