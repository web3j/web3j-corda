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
package org.web3j.corda.model

import javax.annotation.Generated

/**
*
* @param quantity total amount in minor units
* @param displayTokenSize
* @param token
* @param tokenType
*/
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-12-05T15:01:02.723Z"
)
data class Amount(
    /* total amount in minor units */
    val quantity: kotlin.Int,
    val displayTokenSize: java.math.BigDecimal,
    val token: kotlin.String,
    val tokenType: kotlin.String? = null
)
