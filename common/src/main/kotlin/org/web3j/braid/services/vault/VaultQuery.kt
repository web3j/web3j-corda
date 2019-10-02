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
package org.web3j.braid.services.vault

import javax.annotation.Generated

/**
 *
 * @param criteria
 * @param paging
 * @param sorting
 * @param contractStateType Java class name
 */
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-10-01T10:36:39.394Z"
)
data class VaultQuery(
    /* Java class name */
    val contractStateType: kotlin.String,
    val criteria: kotlin.Any? = null,
    val paging: org.web3j.corda.model.core.node.services.vault.PageSpecification? = null,
    val sorting: org.web3j.corda.model.core.node.services.vault.Sort? = null
)
