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
package org.web3j.corda.codegen

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.exists
import java.io.File
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class CorDappGeneratorTest {

    @TempDir
    lateinit var outputDir: File

    @Test
    fun `generate from CordApp name and package`() {
        CorDappGenerator("org.web3j.corda", "Sample", outputDir, "0.1.0-SNAPSHOT").generate().apply {
            assertThat(map { it.absolutePath }).containsAll(
                File(outputDir, KOTLIN_SOURCE.format("contracts", "main", "contracts/", "SampleContract")).absolutePath,
                File(outputDir, KOTLIN_SOURCE.format("contracts", "test", "contracts/", "ContractTests")).absolutePath,
                File(outputDir, KOTLIN_SOURCE.format("contracts", "main", "states/", "SampleState")).absolutePath,
                File(outputDir, "contracts/build.gradle").absolutePath,
                File(outputDir, KOTLIN_SOURCE.format("workflows", "main", "flows/", "Flows")).absolutePath,
                File(outputDir, KOTLIN_SOURCE.format("workflows", "test", "", "FlowTests")).absolutePath,
                File(outputDir, KOTLIN_SOURCE.format("workflows", "test", "", "ContractTests")).absolutePath,
                File(outputDir, "workflows/build.gradle").absolutePath
            ).also {
                forEach { assertThat(it).exists() }
            }
        }
    }

    companion object {
        const val KOTLIN_SOURCE = "%s/src/%s/kotlin/org/web3j/corda/%s%s.kt"
    }
}
