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

class CordaCommandTest {

    @TempDir
    lateinit var outputDir: File

    @Test
    @Disabled("Braid class loader issue")
    fun generateFromCorDappsDir() {
        CordaCommandMain.main(
            "generate",
            "-p", "org.webj3.corda.examples.obligation",
            "-d",
            "/Users/xavier/Development/Projects/Web3Labs/web3j-corda-samples/kotlin-source/build/nodes/PartyA/cordapps",
            "-o",
            outputDir.absolutePath
        )
    }

    @Test
    fun generateFromOpenApiSpec() {
        CordaCommandMain.main(
            "generate",
            "-p", "org.webj3.corda.examples.obligation",
            "-u", javaClass.classLoader.getResource("swagger.json")!!.toExternalForm(),
            "-o", outputDir.absolutePath
        )

        assertThat(File(outputDir, "src/main/kotlin/org/webj3/corda/examples/obligation/api/CordaCore.kt")).exists()
        // FIXME assertThat(File(outputDir, "src/main/kotlin/org/webj3/corda/examples/obligation/api/Obligation.kt")).exists()
    }
}
