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

import assertk.assertThat
import assertk.assertions.exists
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * TODO Implement more tests and add assertions.
 */
class CorDappGeneratorTest {

    @TempDir
    lateinit var outputDir: File

    @Test
    fun `generate Obligation CorDapp`() {

        val definition = javaClass.classLoader.getResource("Obligation.json")?.run {
            File(file).readText()
        } ?: fail { "Obligation.json" }

        CorDappGenerator("org.web3j.corda", definition, outputDir).generate()

        File(outputDir, OUTPUT_PATH.format("main", "obligation", "ObligationCordapp", "")).also {
            assertThat(it).exists()
        }

        File(outputDir, OUTPUT_PATH.format("test", "obligation", "ObligationCordapp", "Test")).also {
            assertThat(it).exists()
        }

        File(outputDir, OUTPUT_PATH.format("main", "test", "TestCordapp", "")).also {
            assertThat(it).exists()
        }

        File(outputDir, OUTPUT_PATH.format("test", "test", "TestCordapp", "Test")).also {
            assertThat(it).exists()
        }
    }

    companion object {
        const val OUTPUT_PATH = "src/%s/kotlin/org/web3j/corda/%s/cordapp/api/%s%s.kt"
    }
}
