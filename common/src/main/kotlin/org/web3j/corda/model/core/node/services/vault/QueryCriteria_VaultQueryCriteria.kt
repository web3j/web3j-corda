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
package org.web3j.corda.model.core.node.services.vault

import javax.annotation.Generated

/**
 *
 * @param status
 * @param contractStateTypes
 * @param stateRefs
 * @param notary
 * @param softLockingCondition
 * @param timeCondition
 * @param relevancyStatus
 * @param constraintTypes
 * @param constraints
 * @param participants
 */
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-09-25T12:12:09.61Z"
)
data class QueryCriteria_VaultQueryCriteria(
    val status: org.web3j.corda.model.core.node.services.vault.QueryCriteria_VaultQueryCriteria.Status,
    val relevancyStatus: org.web3j.corda.model.core.node.services.vault.QueryCriteria_VaultQueryCriteria.RelevancyStatus,
    val constraintTypes: org.web3j.corda.model.core.node.services.vault.QueryCriteria_VaultQueryCriteria.ConstraintTypes,
    val constraints: List<org.web3j.corda.model.core.node.services.Vault_ConstraintInfo>,
    val contractStateTypes: List<String>? = null,
    val stateRefs: List<org.web3j.corda.model.core.contracts.StateRef>? = null,
    val notary: List<org.web3j.corda.model.core.identity.AbstractParty>? = null,
    val softLockingCondition: org.web3j.corda.model.core.node.services.vault.QueryCriteria_SoftLockingCondition? = null,
    val timeCondition: org.web3j.corda.model.core.node.services.vault.QueryCriteria_TimeCondition? = null,
    val participants: List<org.web3j.corda.model.core.identity.AbstractParty>? = null
) {
    enum class Status {
        UNCONSUMED,
        CONSUMED,
        ALL
    }

    enum class RelevancyStatus {
        RELEVANT,
        NOT_RELEVANT,
        ALL
    }

    enum class ConstraintTypes {
        ALWAYS_ACCEPT,
        HASH,
        CZ_WHITELISTED,
        SIGNATURE
    }
}
