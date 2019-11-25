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

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class CorDappGenerator(
    private val packageName: String,
    private val corDappName: String,
    private val outputDir: File,
    private val version: String
) : CordaGenerator {

    private val context = mutableMapOf<String, Any>()

    override fun generate(): List<File> {
        context["packageName"] = packageName
        context["corDappName"] = corDappName
        context["web3jCordaVersion"] = version
        context["generator"] = "org.web3j.corda.codegen.CorDappGenerator"
        context["currentDate"] = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME)
        CordaGeneratorUtils.addLambdas(context)

        return generateTemplateFlow() + generateTemplateContract() + generateGradleFiles() + generateClientTests()
    }

    private fun generateGradleFiles(): List<File> {
        return listOf(
            generateFromTemplate(
                "",
                "build.gradle",
                mustacheTemplate("cordapp_gradle.mustache")
            ),
            generateFromTemplate(
                "",
                "gradle.properties",
                mustacheTemplate("cordapp_gradle_properties.mustache")
            ),
            generateFromTemplate(
                "clients",
                "build.gradle",
                mustacheTemplate("client/cordapp_client_gradle.mustache")
            )
        )
    }

    private fun generateTemplateContract(): List<File> {
        return listOf(
            generateFromTemplate(
                "${stubPath.format("contracts", "main")}contracts",
                "${corDappName}Contract.kt",
                mustacheTemplate("contracts/contract.mustache")
            ),
            generateFromTemplate(
                "${stubPath.format("contracts", "main")}states",
                "${corDappName}State.kt",
                mustacheTemplate("contracts/state.mustache")
            ),
            generateFromTemplate(
                "${stubPath.format("contracts", "test")}contracts",
                "ContractTests.kt",
                mustacheTemplate("contracts/contract_test.mustache")
            ),
            generateFromTemplate(
                "contracts",
                "build.gradle",
                mustacheTemplate("contracts/gradle_build.mustache")
            )
        )
    }

    private fun generateTemplateFlow(): List<File> {
        return listOf(
            generateFromTemplate(
                "${stubPath.format("workflows", "main")}flows",
                "Flows.kt",
                mustacheTemplate("workflows/flow.mustache")
            ),
            generateFromTemplate(
                stubPath.format("workflows", "test"),
                "FlowTests.kt",
                mustacheTemplate("workflows/flow_test.mustache")
            ),
            generateFromTemplate(
                stubPath.format("workflows", "test"),
                "ContractTests.kt",
                mustacheTemplate("workflows/flow_contract_test.mustache")
            ),
            generateFromTemplate(
                "workflows",
                "build.gradle",
                mustacheTemplate("workflows/gradle_build.mustache")
            ),
            generateFromTemplate(
                stubPath.format("workflows", "test"),
                "NodeDriver.kt",
                mustacheTemplate("workflows/node_driver.mustache")
            )
        )
    }

    private fun generateClientTests(): List<File> {
        return listOf(
            generateFromTemplate(
                stubPath.format("clients", "test") + "workflows${File.separator}api",
                "WorkflowsTest.kt",
                mustacheTemplate("client/cordapp_client_new_test.mustache")
            )
        )
    }

    private fun generateFromTemplate(path: String, name: String, template: Template): File {
        return File(outputDir, path)
            .apply { mkdirs() }
            .resolve(name)
            .apply {
                mustacheWriter(template, absolutePath)
                if (name.endsWith(".kt")) {
                    CordaGeneratorUtils.kotlinFormat(this)
                }
            }
    }

    private fun mustacheWriter(template: Template, filePath: String) {
        PrintWriter(OutputStreamWriter(FileOutputStream(filePath))).use {
            template.execute(context, it)
            it.flush()
        }
    }

    private fun mustacheTemplate(file: String): Template {
        return javaClass.classLoader.getResourceAsStream("$TEMPLATE_DIR/$file")?.run {
            compiler.compile(InputStreamReader(this))
        } ?: throw IllegalStateException("Template not found: $TEMPLATE_DIR/$file")
    }

    private val stubPath = (listOf("%s", "src", "%s", "kotlin") + packageName.split("."))
        .joinToString(File.separator) + File.separator

    companion object {
        private const val TEMPLATE_DIR = "cordapp"

        private val compiler = Mustache.compiler()
    }
}
