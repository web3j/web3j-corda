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
package org.web3j.corda.console

import assertk.assertThat
import assertk.assertions.exists
import assertk.fail
import java.io.File
import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ResultHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class NewCommandTest {

    @TempDir
    lateinit var outputDir: File

    @Test
    fun `create a template CorDapp and verify build`() {
        CordaCommandMain.main(
            "new",
            "-p", "org.web3j.corda",
            "-n", "Sample",
            "-o", outputDir.absolutePath
        )
        File(outputDir, "build/libs").also {
            assertThat(it).exists()
        }
        GradleConnector.newConnector()
            .useBuildDistribution()
            .forProjectDirectory(outputDir)
            .connect()
            .newBuild()
            .forTasks("build")
            .run(object : ResultHandler<Void> {
                override fun onFailure(failure: GradleConnectionException) {
                    fail(failure.message ?: failure.javaClass.canonicalName)
                }

                override fun onComplete(result: Void) {}
            })
    }
}
