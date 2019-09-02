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
package org.web3j.corda.codegen.generated.obligation.model

/**
*
 * @param commonName
 * @param organisationUnit
 * @param organisation
 * @param locality
 * @param state
 * @param country
 * @param x500Principal
*/
data class CordaX500Name(
    val commonName: kotlin.String? = null,
    val organisationUnit: kotlin.String? = null,
    val organisation: kotlin.String? = null,
    val locality: kotlin.String? = null,
    val state: kotlin.String? = null,
    val country: kotlin.String? = null,
    val x500Principal: X500Principal? = null
)
