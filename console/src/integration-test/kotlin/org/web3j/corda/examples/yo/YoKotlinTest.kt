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
package org.web3j.corda.examples.yo

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.web3j.corda.examples.network
import org.web3j.corda.yo.YoFlowPayload
import org.web3j.corda.yo.api.Yo

class YoKotlinTest {

    @Test
    fun `send Yo`() {
        val partyB = network.parties[0].corda.api.network.nodes
            .findByX500Name("O=PartyB, L=New York, C=US")[0].legalIdentities[0]

        Yo.load(network.parties[0].corda.service).flows.yoFlow.start(
            YoFlowPayload(partyB)
        ).apply {
            assertThat(coreTransaction!!.outputs[0].data!!.participants?.first()?.owningKey).isEqualTo(partyB.owningKey)
        }
    }
}
