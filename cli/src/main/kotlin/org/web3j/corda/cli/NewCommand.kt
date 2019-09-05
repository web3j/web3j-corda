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

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.ruleset.experimental.ExperimentalRuleSetProvider
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import picocli.CommandLine.Command
import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import mu.KLogging
import picocli.CommandLine.Option
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.io.Writer
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Custom CLI interpreter to generate a new sample CordApp and web3j client.
 */
@Command(name = "new")
class NewCommand : CommonCommand() {

    @Option(
        names = ["-n", "--name"],
        description = ["CordApp name"],
        required = true
    )
    lateinit var corDappName: String

    private val corDappTemplate = mustacheTemplate("CorDapp.mustache")
    private val gradleTemplate = mustacheTemplate("gradle.mustache")

    override fun run() {
        generateFlow()
        generateGradleFile()
        Files.copy(
            javaClass.classLoader.getResource("constants.properties")?.openStream()!!,
            outputDir.resolve("constants.properties").toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
    }

    private fun mustacheTemplate(file: String): Template {
        return Mustache.compiler().compile(
            InputStreamReader(
                javaClass.classLoader
                    .getResourceAsStream(file)!!
            )
        )
    }

    private fun generateFlow() {
        File(outputDir, "src/main/kotlin/${packageName.replace(".", "/")}")
            .apply { mkdirs() }
            .resolve("$corDappName.kt")
            .apply {
                val context = mapOf(
                    "lowercase" to LowercaseLambda,
                    "PackageName" to packageName,
                    "CorDappName" to corDappName
                )

                mustacheWriter(corDappTemplate, absolutePath, context)

                KtLint.format(
                    KtLint.Params(
                        ruleSets = ruleSets,
                        cb = { error, _ ->
                            logger.warn { error }
                        },
                        text = readText(),
                        debug = true
                    )
                ).run {
                    writeText(this)
                }
            }
    }

    private fun generateGradleFile() {
        mustacheWriter(
            gradleTemplate,
            "${outputDir}/build.gradle",
            context = mapOf(
                "CorDappName" to corDappName
            )
        )
    }

    private fun mustacheWriter(template: Template, filePath: String, context: Map<String, Any>) {
        PrintWriter(OutputStreamWriter(FileOutputStream(filePath))).use {
            template.execute(
                context,
                it
            )
            it.flush()
        }
    }

    companion object : KLogging() {
        private val ruleSets = listOf(
            StandardRuleSetProvider().get(),
            ExperimentalRuleSetProvider().get()
        )

        object LowercaseLambda : Mustache.Lambda {
            override fun execute(fragment: Template.Fragment, writer: Writer) {
                writer.write(fragment.execute().toLowerCase())
            }
        }
    }

}
