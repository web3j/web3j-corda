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
package org.web3j.corda.protocol

import org.web3j.corda.api.CordaApi

/**
 * Entry class for the Corda API.
 */
class Corda private constructor(
    val api: CordaApi,
    val service: CordaService
) {
    companion object {

        /**
         * Build a new Corda instance with the specified service.
         */
        @JvmStatic
        fun build(service: CordaService): Corda {
            return ClientBuilder.build(
                CordaApi::class.java, service, CordaException.Companion::of
            ).let {
                Corda(it, service)
            }
        }
    }
}
