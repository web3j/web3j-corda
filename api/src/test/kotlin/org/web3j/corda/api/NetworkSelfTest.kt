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
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import javax.ws.rs.core.MediaType
import org.apache.http.client.methods.HttpGet
import org.junit.jupiter.api.Test
import org.web3j.braid.services.SimpleNodeInfo
import org.web3j.corda.model.core.identity.Party
import org.web3j.corda.model.core.utilities.NetworkHostAndPort

class NetworkSelfTest : WireMockInterface() {

    @Test
    fun `get self node info`() {

        val request = HttpGet("http://localhost:8080/api/rest/network/nodes/self")
        request.addHeader("Content-Type", MediaType.APPLICATION_JSON)

        val expectedSelf =
            SimpleNodeInfo(listOf(NetworkHostAndPort("http://localhost", 8090)), listOf(Party("PartyA", "")))

        stubFor(
            get(urlPathEqualTo("/api/rest/network/nodes/self"))
                .withHeader("Content-Type", matching(MediaType.APPLICATION_JSON))
                .willReturn(okJson(objectMapper.writeValueAsString(expectedSelf)))
        )

        val httpResponse = httpClient.execute(request)

        assertThat(httpResponse.statusLine.statusCode).isEqualTo(200)

        val actualSelf = objectMapper.readValue(httpResponse.entity.content, SimpleNodeInfo::class.java)

        assertThat(actualSelf).isEqualTo(expectedSelf)

        verify(
            getRequestedFor(urlMatching("/api/rest/network/nodes/self"))
                .withHeader("Content-Type", matching(MediaType.APPLICATION_JSON))
        )
    }
}
