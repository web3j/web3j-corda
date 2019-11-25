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
package org.web3j.corda.codegen

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.web3j.corda.util.sanitizeCorDappName

class CordaGeneratorUtilsTest {

    @ParameterizedTest
    @ValueSource(
        strings = [
            "/cordapps/workflows-test-0.1-SNAPSHOT/flows/org.web3j.corda.flows.Initiator",
            "/cordapps/workflows-test/flows/org.web3j.corda.flows.Initiator",
            "/cordapps/workflows-test-0.1/flows/org.web3j.corda.flows.Initiator",
            "/cordapps/workflows-test-0.1.0/flows/org.web3j.corda.flows.Initiator"
        ]
    )
    fun `Correctly sanitize CorDapp name`(path: String) {
        val expected = "/cordapps/workflows-test/flows/org.web3j.corda.flows.Initiator"
        assertThat(sanitizeCorDappName(path)).isEqualTo(expected)
    }
}
