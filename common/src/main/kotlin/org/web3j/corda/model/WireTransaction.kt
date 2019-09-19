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
package org.web3j.corda.model

data class WireTransaction(
    /**
     * Base 58 Encoded Secure Hash, eg. `GfHq2tTVk9z4eXgyUuofmR16H6j7srXt8BCyidKdrZL5JEwFqHgDSuiinbTE`.
     */
    val attachments: List<String>,
    val inputs: List<StateRef>,
    val references: List<StateRef>,
    val outputs: List<TransactionStateContractState>,
    val commands: List<CommandObject>,
    val notary: Party,
    val timeWindow: TimeWindow,
    /**
     * Base 58 Encoded Secure Hash.
     */
    val networkParametersHash: String,
    val componentGroups: List<ComponentGroup>,
    /**
     * Hex encoded Byte Array.
     */
    val privacySalt: String,
    /**
     * Base 58 Encoded Secure Hash.
     */
    val id: String,
    /**
     * Base 58 Encoded Secure Hash, eg. `GfHq2tTVk9z4eXgyUuofmR16H6j7srXt8BCyidKdrZL5JEwFqHgDSuiinbTE`.
     */
    val `groupHashes$core`: List<String>,
    /**
     * Base 58 Encoded Secure Hash, eg. `GfHq2tTVk9z4eXgyUuofmR16H6j7srXt8BCyidKdrZL5JEwFqHgDSuiinbTE`.
     */
    val `groupsMerkleRoots$core`: List<String>,
    /**
     * Base 58 Encoded Secure Hash, eg. `GfHq2tTVk9z4eXgyUuofmR16H6j7srXt8BCyidKdrZL5JEwFqHgDSuiinbTE`.
     */
    val `availableComponentNonces$core`: List<String>,
    /**
     * Base 58 Encoded Secure Hash, eg. `GfHq2tTVk9z4eXgyUuofmR16H6j7srXt8BCyidKdrZL5JEwFqHgDSuiinbTE`.
     */
    val `availableComponentHashes$core`: List<String>,
    val merkleTree: MerkleTree,
    /**
     * Base 58 Encoded Secure Hash, eg. `GfHq2tTVk9z4eXgyUuofmR16H6j7srXt8BCyidKdrZL5JEwFqHgDSuiinbTE`.
     */
    val requiredSigningKeys: Set<String>,
    val availableComponentGroups: List<List<Any>>,
    val outputStates: List<ContractState>
)
