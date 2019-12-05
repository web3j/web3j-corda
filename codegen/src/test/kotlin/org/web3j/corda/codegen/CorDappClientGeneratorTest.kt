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

import assertk.all
import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.containsOnly
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.api.io.TempDir
import org.web3j.corda.assertion.hasName
import org.web3j.corda.assertion.hasVoidFunction
import org.web3j.corda.util.KCompiler
import org.web3j.corda.util.KCompilerClassLoader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Paths

class CorDappClientGeneratorTest {

    @Test
    fun `generate from Corda API definition`() {

        val definition = javaClass.classLoader.getResource("corda-api.json")?.run {
            openStream().use { InputStreamReader(it).readText() }
        } ?: fail { "corda-api.json" }

        CorDappClientGenerator("org.web3j.corda", definition, outputDir, true).generate().apply {
            assertThat(map { it.absolutePath }).containsAll(
                loadSource("main", "core", "CordaCore", "").absolutePath,
                loadSource("test", "core", "CordaCore", "Test").absolutePath,
                loadSource("main", "finance/workflows", "CordaFinanceWorkflows", "").absolutePath,
                loadSource("test", "finance/workflows", "CordaFinanceWorkflows", "Test").absolutePath
            ).also {
                forEach {
                    assertThat(compiler.compile(it)).isNotEmpty()
                }
            }
        }

        // Check that the model classes are re-packaged correctly
        val modelClass = "generated.net.corda.finance.schemas.CashSchemaV1"
        assertThat(classLoader.loadClass(modelClass).kotlin).isNotNull()
    }

    @Test
    fun `generate from empty Corda API definition`() {

        val definition = javaClass.classLoader.getResource("empty-flow.json")?.run {
            openStream().use { InputStreamReader(it).readText() }
        } ?: fail { "empty-flow.json" }

        CorDappClientGenerator("org.web3j.corda", definition, outputDir, false).generate().apply {
            assertThat(map { it.absolutePath }).containsOnly(
                File(outputDir, KOTLIN_SOURCE.format("main", "workflows", "Workflows", "")).absolutePath
            )
            val initiatorClass = "org.web3j.corda.workflows.api.Workflows\$FlowResource\$Initiator"
            assertThat(classLoader.loadClass(initiatorClass).kotlin).all {
                hasName("org.web3j.corda.workflows.api.Workflows.FlowResource.Initiator")
                hasVoidFunction("start")
            }
            forEach {
                assertThat(compiler.compile(it)).isNotEmpty()
            }
        }
    }

    companion object {

        @TempDir
        lateinit var outputDir: File
        lateinit var classLoader: ClassLoader

        val compiler = KCompiler()

        @BeforeAll
        @JvmStatic
        fun setUp() {
            val mainDir = Paths.get(outputDir.absolutePath, "src", "main", "kotlin")
            val testDir = Paths.get(outputDir.absolutePath, "src", "test", "kotlin")
            classLoader = KCompilerClassLoader(
                arrayOf(
                    mainDir.toUri().toURL(), testDir.toUri().toURL()
                ), compiler
            )
        }

        private fun loadSource(vararg path: String): File {
            return File(outputDir, KOTLIN_SOURCE.format(*path))
        }

        const val KOTLIN_SOURCE = "src/%s/kotlin/org/web3j/corda/%s/api/%s%s.kt"
    }
}
