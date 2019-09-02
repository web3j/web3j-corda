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
package org.web3j.corda.codegen.generated.obligation.model

import org.web3j.corda.model.ComponentGroup
import org.web3j.corda.model.Party
import org.web3j.corda.model.TimeWindow

/**
*
 * @param attachments
 * @param inputs
 * @param references
 * @param outputs
 * @param commands
 * @param notary
 * @param timeWindow
 * @param networkParametersHash
 * @param componentGroups
 * @param privacySalt
 * @param id
 * @param requiredSigningKeys
 * @param merkleTree
 * @param groupHashesDollarCore
 * @param groupsMerkleRootsDollarCore
 * @param availableComponentNoncesDollarCore
 * @param availableComponentHashesDollarCore
 * @param availableComponentGroups
 * @param outputStates
*/
data class WireTransaction(
    val attachments: kotlin.collections.List<SecureHash>? = null,
    val inputs: kotlin.collections.List<StateRef>? = null,
    val references: kotlin.collections.List<StateRef>? = null,
    val outputs: kotlin.collections.List<TransactionStateContractState>? = null,
    val commands: kotlin.collections.List<CommandObject>? = null,
    val notary: Party? = null,
    val timeWindow: TimeWindow? = null,
    val networkParametersHash: SecureHash? = null,
    val componentGroups: kotlin.collections.List<ComponentGroup>? = null,
    val privacySalt: PrivacySalt? = null,
    val id: SecureHash? = null,
    val requiredSigningKeys: kotlin.collections.List<PublicKey>? = null,
    val merkleTree: MerkleTree? = null,
    val groupHashesDollarCore: kotlin.collections.List<SecureHash>? = null,
    val groupsMerkleRootsDollarCore: kotlin.collections.Map<kotlin.String, SecureHash>? = null,
    val availableComponentNoncesDollarCore: kotlin.collections.Map<kotlin.String, kotlin.collections.List<SecureHash>>? = null,
    val availableComponentHashesDollarCore: kotlin.collections.Map<kotlin.String, kotlin.collections.List<SecureHash>>? = null,
    val availableComponentGroups: kotlin.collections.List<kotlin.collections.List<kotlin.Any>>? = null,
    val outputStates: kotlin.collections.List<ContractState>? = null
)
