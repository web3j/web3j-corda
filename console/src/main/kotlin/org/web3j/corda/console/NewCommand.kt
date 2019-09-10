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

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.ruleset.experimental.ExperimentalRuleSetProvider
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import mu.KLogging
import org.apache.commons.io.FileUtils
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
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

    override fun run() {
        val context = mapOf(
            "packageName" to packageName,
            "corDappName" to corDappName
        )
        generateTemplateFlow(context)
        generateTemplateContract(context)

        copyProjectResources()

        GenerateCommand().apply {
            cordaResource = GenerateCommand.CordaResource
            cordaResource.openApiUrl =
                javaClass.classLoader.getResource("swagger.json")!! // FIXME - change to path of jar files
            packageName = this@NewCommand.packageName
            outputDir = File("${this@NewCommand.outputDir}/clients")
            run()
        }

        copyResource("clients/build.gradle", outputDir)
    }

    private fun copyProjectResources() {
        copyResource("gradle.properties", outputDir)
        copyResource("build.gradle", outputDir)
        copyResource("settings.gradle", outputDir)
        copyResource("gradlew", outputDir)
        File("${outputDir.toURI().path}/gradlew").setExecutable(true)

        FileUtils.copyDirectory(
            File(javaClass.classLoader.getResource("gradle")?.toURI()!!.path),
            outputDir.resolve("gradle")
        )


        copyResource("README.md", outputDir)
    }

    private fun copyResource(name: String, outputDir: File) {
        Files.copy(
            javaClass.classLoader.getResource(name)?.openStream()!!,
            outputDir.resolve(name).toPath(),
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

    private fun generateTemplateContract(context: Map<String, Any>) {
        generateFromTemplate(
            "contracts/src/main/${packageName.replace(".", "/")}/contracts",
            "${corDappName}Contract.kt",
            mustacheTemplate("contract/contract.mustache"),
            context
        )

        generateFromTemplate(
            "contracts/src/main/${packageName.replace(".", "/")}/states",
            "${corDappName}State.kt",
            mustacheTemplate("contract/state.mustache"),
            context
        )

        generateFromTemplate(
            "contracts/src/test/${packageName.replace(".", "/")}",
            "ContractTests.kt",
            mustacheTemplate("contractTest.mustache"),
            context
        )
        generateFromTemplate(
            "contracts/src/",
            "build.gradle",
            mustacheTemplate("contract/contractGradle.mustache"),
            context
        )
    }

    private fun generateTemplateFlow(context: Map<String, Any>) {
        generateFromTemplate(
            "workflows/src/main/${packageName.replace(".", "/")}/flows",
            "Flows.kt",
            mustacheTemplate("workflows/flow.mustache"),
            context
        )
        generateFromTemplate(
            "workflows/src/test/${packageName.replace(".", "/")}",
            "FlowTests.kt",
            mustacheTemplate("workflows/flowTest.mustache"),
            context
        )
        generateFromTemplate(
            "workflows/src/test/${packageName.replace(".", "/")}",
            "ContractTests.kt",
            mustacheTemplate("contractTest.mustache"),
            context
        )
        generateFromTemplate(
            "workflows/src/",
            "build.gradle",
            mustacheTemplate("workflows/flowGradle.mustache"),
            context
        )
    }

    private fun generateFromTemplate(path: String, name: String, template: Template, context: Map<String, Any>) {
        File(outputDir, path)
            .apply { mkdirs() }
            .resolve(name)
            .apply {
                mustacheWriter(template, absolutePath, context)

                if (name.endsWith(".kt")) {
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
    }
}
