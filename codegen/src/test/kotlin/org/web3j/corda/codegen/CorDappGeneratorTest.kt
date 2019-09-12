package org.web3j.corda.codegen

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.exists
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class CorDappGeneratorTest {

    @TempDir
    lateinit var outputDir: File

    @Test
    fun `generate from CordApp name and package`() {
        CorDappGenerator("org.web3j.corda", "Sample", outputDir).generate().apply {
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
