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

import org.web3j.corda.model.Party

/**
*
 * @param inputs
 * @param references
 * @param notary
 * @param newNotary
 * @param serializedComponents
 * @param id
 * @param networkParametersHash
 * @param outputs
 * @param outputStates
*/
data class NotaryChangeWireTransaction(
    val inputs: kotlin.collections.List<StateRef>? = null,
    val references: kotlin.collections.List<StateRef>? = null,
    val notary: Party? = null,
    val newNotary: Party? = null,
    val serializedComponents: kotlin.collections.List<OpaqueBytes>? = null,
    val id: SecureHash? = null,
    val networkParametersHash: SecureHash? = null,
    val outputs: kotlin.collections.List<TransactionStateContractState>? = null,
    val outputStates: kotlin.collections.List<ContractState>? = null
)
