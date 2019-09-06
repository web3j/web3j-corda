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
package org.web3j.corda.cli

import assertk.assertThat
import assertk.assertions.exists
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * TODO Update OpenAPI definition and add Obligation CorDapp assertions.
 */
class CordaCommandTest {

    @TempDir
    lateinit var outputDir: File

    @Test
    @Disabled("Braid class loader issue")
    fun `generate Obligation from CorDapps directory`() {
        CordaCommandMain.main(
            "generate",
            "-p", "org.web3j.corda.examples.obligation",
            "-d", javaClass.classLoader.getResource("cordapps")!!.file,
            "-o", outputDir.absolutePath
        )

        File(outputDir, "${OUTPUT_PATH.format("main")}/ObligationCordapp.kt").let {
            assertThat(it).exists()
        }

        File(outputDir, "${OUTPUT_PATH.format("test")}/ObligationCordappTest.kt").let {
            assertThat(it).exists()
        }
    }

    @Test
    fun `generate Obligation from OpenAPI definition`() {
        CordaCommandMain.main(
            "generate",
            "-p", "org.web3j.corda.examples.obligation",
            "-u", javaClass.classLoader.getResource("swagger.json")!!.toExternalForm(),
            "-o", outputDir.absolutePath
        )

        File(outputDir, "${OUTPUT_PATH.format("main")}/CordaCore.kt").also {
            assertThat(it).exists()
        }

        File(outputDir, "${OUTPUT_PATH.format("test")}/CordaCoreTest.kt").also {
            assertThat(it).exists()
        }
    }

    companion object {
        const val OUTPUT_PATH = "src/%s/kotlin/org/web3j/corda/examples/obligation/api"
    }
}
