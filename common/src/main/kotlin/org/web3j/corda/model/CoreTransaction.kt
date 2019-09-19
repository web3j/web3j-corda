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

/**
 *
 * @param inputs
 * @param references
 * @param networkParametersHash Base 58 Encoded Secure Hash
 * @param notary
 * @param outputs
 * @param outputStates
 * @param id Base 58 Encoded Secure Hash
 */
data class CoreTransaction(
    val inputs: kotlin.collections.List<StateRef>,
    val references: kotlin.collections.List<StateRef>,
    val outputs: kotlin.collections.List<ContractState>,
    val outputStates: kotlin.collections.List<ContractState>,
    /* Base 58 Encoded Secure Hash */
    val id: kotlin.String,
/* Base 58 Encoded Secure Hash */
    val networkParametersHash: kotlin.String? = null,
    val notary: Party? = null
)
