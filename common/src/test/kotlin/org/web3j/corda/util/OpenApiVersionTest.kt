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
package org.web3j.corda.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.web3j.corda.util.OpenApiVersion.Companion.fromVersion
import org.web3j.corda.util.OpenApiVersion.v2_0
import org.web3j.corda.util.OpenApiVersion.v3_0
import org.web3j.corda.util.OpenApiVersion.v3_0_1

class OpenApiVersionTest {

    @Test
    fun `build enum from OpenAPI version`() {
        assertThat(fromVersion("2.0")).isEqualTo(v2_0)
        assertThat(fromVersion("3.0")).isEqualTo(v3_0)
        assertThat(fromVersion("3.0.1")).isEqualTo(v3_0_1)
    }

    @Test
    fun `exception thrown from illegal version`() {
        assertThrows<IllegalArgumentException> {
            fromVersion("1.0")
        }
    }

    @Test
    fun `convert Open API version to int`() {
        assertThat(v2_0.toInt()).isEqualTo(2)
        assertThat(v3_0.toInt()).isEqualTo(3)
        assertThat(v3_0_1.toInt()).isEqualTo(3)
    }
}
