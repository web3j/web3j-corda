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
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import org.web3j.braid.services.SimpleNodeInfo

interface NodeResource {

    @get:GET
    @get:Path("self")
    val self: SimpleNodeInfo

    /**
     * Retrieves all nodes.
     */
    @GET
    fun findAll(): List<SimpleNodeInfo>

    /**
     * Retrieves by the supplied host and port, e.g. `localhost:10000`.
     *
     * @param hostAndPort `host:port` for the Corda P2P of the node.
     */
    @GET
    fun findByHostAndPort(
        @QueryParam("host-and-port")
        hostAndPort: String
    ): List<SimpleNodeInfo>

    /**
     * Retrieves by the supplied X500 name, e.g. `O=PartyB, L=New York, C=US`.
     *
     * @param x500Name the X500 name for the node.
     */
    @GET
    fun findByX500Name(
        @QueryParam("x500-name")
        x500Name: String
    ): List<SimpleNodeInfo>
}
