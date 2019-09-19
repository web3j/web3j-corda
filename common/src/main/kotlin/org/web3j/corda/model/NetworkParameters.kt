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
 * @param minimumPlatformVersion
 * @param notaries
 * @param maxMessageSize
 * @param maxTransactionSize
 * @param modifiedTime JSR310 encoded time representation of Instant
 * @param epoch
 * @param whitelistedContractImplementations
 * @param eventHorizon JSR310 encoded time representation of Duration
 * @param packageOwnership
 */
data class NetworkParameters(
    val minimumPlatformVersion: kotlin.Int,
    val notaries: kotlin.collections.List<NotaryInfo>,
    val maxMessageSize: kotlin.Int,
    val maxTransactionSize: kotlin.Int,
    /* JSR310 encoded time representation of Instant */
    val modifiedTime: kotlin.String,
    val epoch: kotlin.Int,
    val whitelistedContractImplementations: kotlin.collections.Map<kotlin.String, kotlin.collections.List<kotlin.String>>,
    /* JSR310 encoded time representation of Duration */
    val eventHorizon: kotlin.String,
    val packageOwnership: kotlin.collections.Map<kotlin.String, kotlin.String>
)
