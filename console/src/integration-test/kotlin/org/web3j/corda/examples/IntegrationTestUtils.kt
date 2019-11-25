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
@file:JvmName("IntegrationTestUtils")

package org.web3j.corda.examples

import java.io.File
import org.web3j.corda.network.network
import org.web3j.corda.network.nodes
import org.web3j.corda.network.notary
import org.web3j.corda.network.party

val network = network {
    directory = File(javaClass.classLoader.getResource("cordapps")!!.file)
    nodes {
        notary {
            name = "O=Notary, L=London, C=GB"
        }
        party {
            name = "O=PartyA, L=London, C=GB"
        }
        party {
            name = "O=PartyB, L=New York, C=US"
        }
    }
}
