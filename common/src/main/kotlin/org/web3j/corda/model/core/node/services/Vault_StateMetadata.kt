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
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-09-25T12:12:09.609Z"
)
data class Vault_StateMetadata(
    val contractStateClassName: String,
    /* JSR310 encoded time representation of Instant */
    val recordedTime: String,
    val status: org.web3j.corda.model.core.node.services.Vault_StateMetadata.Status,
    val ref: org.web3j.corda.model.core.contracts.StateRef? = null,
/* JSR310 encoded time representation of Instant */
    val consumedTime: String? = null,
    val notary: org.web3j.corda.model.core.identity.AbstractParty? = null,
    val lockId: String? = null,
/* JSR310 encoded time representation of Instant */
    val lockUpdateTime: String? = null,
    val relevancyStatus: org.web3j.corda.model.core.node.services.Vault_StateMetadata.RelevancyStatus? = null,
    val constraintInfo: org.web3j.corda.model.core.node.services.Vault_ConstraintInfo? = null
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
