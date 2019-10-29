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
import assertk.assertions.containsAll
import assertk.assertions.exists
import java.io.File
import java.io.InputStreamReader
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.api.io.TempDir

/**
 * TODO Implement more tests and add assertions.
 */
class CorDappClientGeneratorTest {

    @TempDir
    lateinit var outputDir: File

    @Test
    fun `generate from Corda API definition`() {

        val definition = javaClass.classLoader.getResource("corda-api.json")?.run {
            openStream().use { InputStreamReader(it).readText() }
        } ?: fail { "corda-api.json" }

        CorDappClientGenerator("org.web3j.corda", definition, outputDir, true, false).generate().apply {
            assertThat(map { it.absolutePath }).containsAll(
                File(outputDir, KOTLIN_SOURCE.format("main", "core", "CordaCore", "")).absolutePath,
                File(outputDir, KOTLIN_SOURCE.format("test", "core", "CordaCore", "Test")).absolutePath,
                File(
                    outputDir,
                    KOTLIN_SOURCE.format("main", "finance/workflows", "CordaFinanceWorkflows", "")
                ).absolutePath,
                File(
                    outputDir,
                    KOTLIN_SOURCE.format("test", "finance/workflows", "CordaFinanceWorkflows", "Test")
                ).absolutePath
            ).also {
                forEach { assertThat(it).exists() }
            }
        }
    }

    companion object {
        const val KOTLIN_SOURCE = "src/%s/kotlin/org/web3j/corda/%s/api/%s%s.kt"
    }
}
