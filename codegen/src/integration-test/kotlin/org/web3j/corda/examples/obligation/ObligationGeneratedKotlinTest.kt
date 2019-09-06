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
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.web3j.corda.codegen.generated.obligation.api.Obligation
import org.web3j.corda.codegen.generated.obligation.model.IssueObligationInitiatorParameters
import org.web3j.corda.model.SignedTransaction
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.testcontainers.DockerBasedIntegrationTest
import org.web3j.corda.util.convert

class ObligationGeneratedKotlinTest : DockerBasedIntegrationTest() {

    @Test
    fun `issue obligation`() {
        val party = corda.network.nodes.findAll()[2].legalIdentities[0]
        val parameters = IssueObligationInitiatorParameters("$1", party.name!!, false)

        // 1. Normal version, not type-safe
        val issue = corda
            .corDapps.findById("obligation-cordapp")
            .flows.findById("issue-obligation")

        // Potential runtime exception!
        var signedTx = issue.start(parameters).convert<SignedTransaction>()
        assertThat(signedTx.coreTransaction.outputs[0].data.participants.first().owningKey).isEqualTo(party.owningKey)

        // 2. web3j generated version, 100% type-safe
        signedTx = Obligation.load(corda).flows.issue.start(parameters)
        assertThat(signedTx.coreTransaction.outputs[0].data.participants.first().owningKey).isEqualTo(party.owningKey)
    }

    companion object {

        @JvmStatic
        private lateinit var corda: Corda

        @JvmStatic
        private lateinit var service: CordaService

        @BeforeAll
        @JvmStatic
        internal fun setUpClass() {
            service = CordaService("http://localhost:9000/")
            corda = Corda.build(service)
        }

        @AfterAll
        internal fun tearDownClass() {
            service.close()
        }
    }
}
