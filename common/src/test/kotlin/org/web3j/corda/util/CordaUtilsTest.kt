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
import javax.security.auth.x500.X500Principal
import org.junit.jupiter.api.Test

class CordaUtilsTest {

    @Test
    fun `X500 canonical name`() {
        val name = X500Principal("O=Notary, L=New York, C=US")
        assertThat(name.canonicalName).isEqualTo("notary-new-york-us")
    }
}
