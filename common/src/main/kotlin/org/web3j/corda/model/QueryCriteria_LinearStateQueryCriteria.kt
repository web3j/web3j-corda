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

import java.util.UUID

/**
 *
 * @param participants
 * @param uuid
 * @param externalId
 * @param status
 * @param contractStateTypes
 * @param relevancyStatus
 * @param constraintTypes
 * @param constraints
 */
data class QueryCriteria_LinearStateQueryCriteria(
    val status: QueryCriteria_LinearStateQueryCriteria.Status,
    val relevancyStatus: QueryCriteria_LinearStateQueryCriteria.RelevancyStatus,
    val constraintTypes: QueryCriteria_LinearStateQueryCriteria.ConstraintTypes,
    val constraints: kotlin.collections.List<Vault_ConstraintInfo>,
    val participants: kotlin.collections.List<AbstractParty>? = null,
    val uuid: kotlin.collections.List<UUID>? = null,
    val externalId: kotlin.collections.List<kotlin.String>? = null,
    val contractStateTypes: kotlin.collections.List<kotlin.String>? = null
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
