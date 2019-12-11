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
package org.web3j.corda.network

import org.web3j.corda.util.randomPort

@CordaDslMarker
class CordaRpcSettings internal constructor (private val node: CordaNode) {
    /**
     * Corda RPC address for this node, e.g. `notary:10006`.
     */
    val address: String by lazy {
        "${node.container.containerIpAddress}:${node.container.ports[node.rpcSettings.port]}"
    }

    /**
     * Corda RPC admin address for this node, e.g. `notary:10006`.
     */
    val adminAddress: String by lazy {
        "${node.container.containerIpAddress}:${node.container.ports[node.rpcSettings.adminPort]}"
    }

    /**
     * Corda RPC port for this node.
     */
    var port: Int = randomPort()

    /**
     * Corda RPC admin port for this Corda node.
     */
    var adminPort: Int = randomPort()
}
