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
package org.web3j.corda.console

import assertk.assertThat
import assertk.assertions.exists
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * TODO Update OpenAPI definition assertions.
 */
class CordaNewCommandTest {

    @Test
    fun `generate Obligation from CorDapps directory`() {
        CordaCommandMain.main(
            "new",
            "-p", "org.web3j.corda",
            "-n", "Sample",
            "-o", outputDir.absolutePath
        )

        File(outputDir, "build/libs").also {
            assertThat(it).exists()
        }

        val testTask = GradleRunner.create()
            .withProjectDir(outputDir)
            .withArguments("test")
            .forwardOutput()
            .withDebug(false)
            .build()

        assertThat(testTask.task(":test")).isNotNull()
        assertThat(testTask.task(":test")!!.outcome).isEqualTo(SUCCESS)
    }

    companion object {
        @TempDir
        lateinit var outputDir: File
    }
}
