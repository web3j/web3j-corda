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
