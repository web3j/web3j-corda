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
 * @param origin
 * @param trace
 * @param actor
 * @param externalTrace
 * @param impersonatedActor
 */
data class InvocationContext(
    val origin: kotlin.Any? = null,
    val trace: Trace? = null,
    val actor: Actor? = null,
    val externalTrace: Trace? = null,
    val impersonatedActor: Actor? = null
)
