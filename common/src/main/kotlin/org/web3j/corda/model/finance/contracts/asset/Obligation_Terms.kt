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
package org.web3j.corda.model.finance.contracts.asset

import org.web3j.corda.model.core.contracts.Issued
import javax.annotation.Generated

/**
 *
 * @param acceptableContracts
 * @param acceptableIssuedProducts
 * @param dueBefore JSR310 encoded time representation of Instant
 * @param timeTolerance JSR310 encoded time representation of Duration
 * @param product
 */
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-09-25T12:12:09.611Z"
)
data class Obligation_Terms(
    val acceptableContracts: kotlin.collections.List<kotlin.String>,
    val acceptableIssuedProducts: kotlin.collections.List<Issued>,
    /* JSR310 encoded time representation of Instant */
    val dueBefore: kotlin.String,
    /* JSR310 encoded time representation of Duration */
    val timeTolerance: kotlin.String,
    val product: kotlin.Any
)
