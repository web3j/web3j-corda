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

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.client.WireMock.verify
import javax.ws.rs.core.MediaType
import org.apache.http.client.methods.HttpGet
import org.junit.jupiter.api.Test

class CordaApiTest : WireMockInterface() {

    @Test
    fun `corda network test`() {

        val request = HttpGet("http://localhost:8080/api/rest/network")
        request.addHeader("Content-Type", MediaType.APPLICATION_JSON)

        val httpResponse = httpClient.execute(request)

        assertThat(httpResponse.statusLine.statusCode).isEqualTo(200)

        verify(getRequestedFor(urlMatching("/api/rest/network"))
            .withHeader("Content-Type", matching(MediaType.APPLICATION_JSON)))
    }
}
