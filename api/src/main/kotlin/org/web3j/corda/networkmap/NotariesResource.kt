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
package org.web3j.corda.networkmap

import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.MediaType

interface NotariesResource {

    /**
     * Upload a signed NodeInfo object to the network map.
     */
    @POST
    @Path("{notaryType}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    fun create(@PathParam("notaryType") type: NotaryType, nodeInfo: ByteArray): String

    /**
     * Delete a notary with the node key.
     */
    @DELETE
    @Path("{notaryType}")
    @Consumes(MediaType.TEXT_PLAIN)
    fun delete(@PathParam("notaryType") type: NotaryType, nodeKey: String)
}
