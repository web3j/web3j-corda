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
data class QueryCriteria_VaultQueryCriteria(
    val status: QueryCriteria_VaultQueryCriteria.Status,
    val relevancyStatus: QueryCriteria_VaultQueryCriteria.RelevancyStatus,
    val constraintTypes: QueryCriteria_VaultQueryCriteria.ConstraintTypes,
    val constraints: kotlin.collections.List<Vault_ConstraintInfo>,
    val contractStateTypes: kotlin.collections.List<kotlin.String>? = null,
    val stateRefs: kotlin.collections.List<StateRef>? = null,
    val notary: kotlin.collections.List<AbstractParty>? = null,
    val softLockingCondition: QueryCriteria_SoftLockingCondition? = null,
    val timeCondition: QueryCriteria_TimeCondition? = null,
    val participants: kotlin.collections.List<AbstractParty>? = null
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
