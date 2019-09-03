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

data class CoreTransaction(
    val componentGroups: List<ComponentGroup>,
    val privacySalt: String,
    val attachments: List<String>,
    val inputs: List<StateRef>,
    val references: List<StateRef>,
    val outputs: List<TransactionStateContractState>,
    val outputStates: List<ContractState>,
    val commands: List<Commands>,
    val notary: Party,
    val timeWindow: TimeWindow,
    val networkParametersHash: String,
    val id: String,
    val requiredSigningKeys: List<String>,
    val `groupHashes$core`: List<String>,
    val `groupsMerkleRoots$core`: Map<String, String>,
    val `availableComponentNonces$core`: Map<String, List<String>>,
    val `availableComponentHashes$core`: Map<String, List<String>>,
    val availableComponentGroups: List<List<Any?>?>
)
