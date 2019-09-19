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

data class NotaryChangeWireTransaction(
    val inputs: StateRef,
    val references: StateRef,
    val notary: Party,
    val newNotary: Party,
    /**
     * Hex encoded Byte Array, eg. `736F6D654279746573`.
     */
    val serializedComponents: List<String>,
    val id: String,
    val outputs: TransactionStateContractState,
    val networkParametersHash: String,
    val outputStates: List<ContractState>
)
