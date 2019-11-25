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

import javax.ws.rs.Path

/**
 * Network Map Service client (partial implementation).
 *
 * **Please note:** The protected parts of this API require JWT authentication.
 *
 * @see [AdminResource.login]
 */
interface NetworkMapApi {

    @get:Path("network-map")
    val networkMap: NetworkMapResource

    @get:Path("certificate")
    val certificate: CertificateResource

    @get:Path("certman/api")
    val certMan: CertManResource

    @get:Path("admin/api")
    val admin: AdminResource
}
