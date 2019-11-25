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
package org.web3j.corda.model.core.transactions

import javax.annotation.Generated

/**
 *
 * @param privacySalt
 * @param attachments
 * @param inputs
 * @param references
 * @param outputs
 * @param commands
 * @param notary
 * @param timeWindow
 * @param networkParametersHash Base 58 Encoded Secure Hash
 * @param id Base 58 Encoded Secure Hash
 * @param requiredSigningKeys
 * @param groupHashesDollarCore
 * @param groupsMerkleRootsDollarCore
 * @param availableComponentNoncesDollarCore
 * @param availableComponentHashesDollarCore
 */
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-10-15T13:26:15.484Z"
)
data class WireTransaction(
    val attachments: List<String>,
    val inputs: List<org.web3j.corda.model.core.contracts.StateRef>,
    val references: List<org.web3j.corda.model.core.contracts.StateRef>,
    val outputs: List<org.web3j.corda.model.core.contracts.TransactionState_net_corda_core_contracts_ContractState>,
    val commands: List<org.web3j.corda.model.core.contracts.Command_Object>,
    /* Base 58 Encoded Secure Hash */
    val id: String,
    val requiredSigningKeys: List<String>,
    val groupHashesDollarCore: List<String>,
    val groupsMerkleRootsDollarCore: Map<String, String>,
    val availableComponentNoncesDollarCore: Map<String, List<String>>,
    val availableComponentHashesDollarCore: Map<String, List<String>>,
    val privacySalt: org.web3j.corda.model.core.contracts.PrivacySalt? = null,
    val notary: org.web3j.corda.model.core.identity.Party? = null,
    val timeWindow: org.web3j.corda.model.core.contracts.TimeWindow? = null,
/* Base 58 Encoded Secure Hash */
    val networkParametersHash: String? = null
)
