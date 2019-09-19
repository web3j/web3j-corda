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
 * @param externalId
 * @param uuid
 * @param stateRef
 */
data class CommonSchemaV1_LinearState(
    val uuid: UUID,
    val participants: kotlin.collections.List<AbstractParty>? = null,
    val externalId: kotlin.String? = null,
    val stateRef: PersistentStateRef? = null
)
