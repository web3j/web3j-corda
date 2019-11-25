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
package org.web3j.corda.model.node.services.vault

import javax.annotation.Generated

/**
 *
 * @param notary
 * @param contractStateClassName
 * @param stateStatus
 * @param recordedTime JSR310 encoded time representation of Instant
 * @param consumedTime JSR310 encoded time representation of Instant
 * @param lockId
 * @param relevancyStatus
 * @param lockUpdateTime JSR310 encoded time representation of Instant
 * @param constraintType
 * @param constraintData
 * @param stateRef
 */
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-09-25T12:12:09.611Z"
)
data class VaultSchemaV1_VaultStates(
    val contractStateClassName: String,
    val stateStatus: org.web3j.corda.model.node.services.vault.VaultSchemaV1_VaultStates.StateStatus,
    /* JSR310 encoded time representation of Instant */
    val recordedTime: String,
    val relevancyStatus: org.web3j.corda.model.node.services.vault.VaultSchemaV1_VaultStates.RelevancyStatus,
    val constraintType: org.web3j.corda.model.node.services.vault.VaultSchemaV1_VaultStates.ConstraintType,
    val notary: org.web3j.corda.model.core.identity.Party? = null,
/* JSR310 encoded time representation of Instant */
    val consumedTime: String? = null,
    val lockId: String? = null,
/* JSR310 encoded time representation of Instant */
    val lockUpdateTime: String? = null,
    val constraintData: List<ByteArray>? = null,
    val stateRef: org.web3j.corda.model.core.schemas.PersistentStateRef? = null
) {
    enum class StateStatus {
        UNCONSUMED,
        CONSUMED,
        ALL
    }

    enum class RelevancyStatus {
        RELEVANT,
        NOT_RELEVANT,
        ALL
    }

    enum class ConstraintType {
        ALWAYS_ACCEPT,
        HASH,
        CZ_WHITELISTED,
        SIGNATURE
    }
}
