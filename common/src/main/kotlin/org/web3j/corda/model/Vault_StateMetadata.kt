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
 * @param ref
 * @param contractStateClassName
 * @param recordedTime JSR310 encoded time representation of Instant
 * @param consumedTime JSR310 encoded time representation of Instant
 * @param status
 * @param notary
 * @param lockId
 * @param lockUpdateTime JSR310 encoded time representation of Instant
 * @param relevancyStatus
 * @param constraintInfo
 */
data class Vault_StateMetadata(
    val contractStateClassName: kotlin.String,
    /* JSR310 encoded time representation of Instant */
    val recordedTime: kotlin.String,
    val status: Vault_StateMetadata.Status,
    val ref: StateRef? = null,
/* JSR310 encoded time representation of Instant */
    val consumedTime: kotlin.String? = null,
    val notary: AbstractParty? = null,
    val lockId: kotlin.String? = null,
/* JSR310 encoded time representation of Instant */
    val lockUpdateTime: kotlin.String? = null,
    val relevancyStatus: Vault_StateMetadata.RelevancyStatus? = null,
    val constraintInfo: Vault_ConstraintInfo? = null
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
}
