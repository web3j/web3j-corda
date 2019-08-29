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
package org.web3j.corda.api

import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class CordaApiIntegrationTest {

    companion object {

        @Container
        @JvmStatic
        private val CORDA =
            KGenericContainer("corda/corda-zulu-4.1:latest")
//            .withClasspathResourceMapping("aion/config", "/aion/custom/config", READ_WRITE)
//            .withClasspathResourceMapping("aion/log", "/aion/custom/log", READ_WRITE)
//            .withCommand("/aion/aion.sh --network custom")
//            .withExposedPorts(8545)

        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
    }
}
