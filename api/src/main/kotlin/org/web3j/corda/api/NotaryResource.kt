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
package org.web3j.corda.api

import javax.ws.rs.GET
import javax.ws.rs.QueryParam
import org.web3j.corda.model.core.identity.Party

interface NotaryResource {

    /**
     * Retrieves all notaries.
     */
    @GET
    fun findAll(): List<Party>

    /**
     * Retrieves by the supplied X500 name, e.g. `O=PartyB, L=New York, C=US`.
     *
     * @param x500Name the X500 name for the node.
     */
    @GET
    fun findByX500Name(
        @QueryParam("x500-name")
        x500Name: String
    ): List<Party>
}
