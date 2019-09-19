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
 * @param expression
 * @param status
 * @param contractStateTypes
 * @param relevancyStatus
 * @param constraintTypes
 * @param constraints
 * @param participants
 */
data class QueryCriteria_VaultCustomQueryCriteria(
    val status: QueryCriteria_VaultCustomQueryCriteria.Status,
    val relevancyStatus: QueryCriteria_VaultCustomQueryCriteria.RelevancyStatus,
    val constraintTypes: QueryCriteria_VaultCustomQueryCriteria.ConstraintTypes,
    val constraints: kotlin.collections.List<Vault_ConstraintInfo>,
    val expression: kotlin.Any? = null,
    val contractStateTypes: kotlin.collections.List<kotlin.String>? = null,
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
