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
 * @param states
 * @param statesMetadata
 * @param totalStatesAvailable
 * @param stateTypes
 * @param otherResults
 */
data class Vault_Page(
    val states: kotlin.collections.List<ContractState>,
    val statesMetadata: kotlin.collections.List<Vault_StateMetadata>,
    val totalStatesAvailable: kotlin.Long,
    val stateTypes: Vault_Page.StateTypes,
    val otherResults: kotlin.collections.List<kotlin.Any>
) {
    enum class StateTypes {
        UNCONSUMED,
        CONSUMED,
        ALL
    }
}
