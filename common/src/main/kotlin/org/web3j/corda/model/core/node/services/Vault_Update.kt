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
package org.web3j.corda.model.core.node.services

import javax.annotation.Generated

/**
 *
 * @param consumed
 * @param produced
 * @param flowId
 * @param type
 * @param references
 * @param empty
 */
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-09-25T12:12:09.609Z"
)
data class Vault_Update(
    val consumed: List<org.web3j.corda.model.core.contracts.StateAndRef_net_corda_core_contracts_ContractState>,
    val produced: List<org.web3j.corda.model.core.contracts.StateAndRef_net_corda_core_contracts_ContractState>,
    val type: org.web3j.corda.model.core.node.services.Vault_Update.Type,
    val references: List<org.web3j.corda.model.core.contracts.StateAndRef_net_corda_core_contracts_ContractState>,
    val empty: Boolean,
    val flowId: java.util.UUID? = null
) {
    enum class Type {
        GENERAL,
        NOTARY_CHANGE,
        CONTRACT_UPGRADE
    }
}
