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
package org.web3j.corda.codegen

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.api.io.TempDir
import java.io.File

class CorDappGeneratorTest {

    @TempDir
    lateinit var output: File

    @Test
    fun `generate with mustache`() {

        val obligation = javaClass.classLoader.getResource("Obligation.json")

        val outputDir = "build/generated"
        val generator = CorDappGenerator(
            "obligation-cordapp",
            "org.web3j.corda.codegen.generated.obligation",
            obligation?.file ?: fail { "Obligation.json" },
            outputDir
        )

        generator.generate()
    }
}
