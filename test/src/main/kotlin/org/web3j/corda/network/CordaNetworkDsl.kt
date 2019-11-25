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

import java.util.function.Consumer

/**
 * Corda network DSL entry point extension method.
 */
@CordaDslMarker
fun network(networkBlock: CordaNetwork.() -> Unit): CordaNetwork {
    return CordaNetwork.networkJava(Consumer { networkBlock.invoke(it) })
}

@CordaDslMarker
fun CordaNetwork.nodes(nodesBlock: CordaNodes.() -> Unit) {
    nodesJava(Consumer { nodesBlock.invoke(it) })
}

@CordaDslMarker
fun CordaNodes.party(partyBlock: CordaPartyNode.() -> Unit) {
    partyJava(Consumer { partyBlock.invoke(it) })
}

@CordaDslMarker
fun CordaNodes.notary(notaryBlock: CordaNotaryNode.() -> Unit) {
    notaryJava(Consumer { notaryBlock.invoke(it) })
}

@CordaDslMarker
fun CordaNode.rpcSettings(rpcSettingsBlock: CordaRpcSettings.() -> Unit) {
    rpcSettingsJava(Consumer { rpcSettingsBlock.invoke(it) })
}

@CordaDslMarker
fun CordaNode.rpcUsers(rpcUsersBlock: CordaRpcUsers.() -> Unit) {
    rpcUsersJava(Consumer { rpcUsersBlock.invoke(it) })
}

@DslMarker
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
internal annotation class CordaDslMarker
