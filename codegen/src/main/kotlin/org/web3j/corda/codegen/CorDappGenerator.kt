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
package org.web3j.corda.codegen

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter

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
        CordaGeneratorUtils.addLambdas(context)

        return generateTemplateFlow() + generateTemplateContract() + generateGradleFiles()
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
                "contracts/src/main/kotlin/${packageName.replace(".", "/")}/contracts",
                "${corDappName}Contract.kt",
                mustacheTemplate("contracts/contract.mustache")
            ),
            generateFromTemplate(
                "contracts/src/main/kotlin/${packageName.replace(".", "/")}/states",
                "${corDappName}State.kt",
                mustacheTemplate("contracts/state.mustache")
            ),
            generateFromTemplate(
                "contracts/src/test/kotlin/${packageName.replace(".", "/")}/contracts",
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
                "workflows/src/main/kotlin/${packageName.replace(".", "/")}/flows",
                "Flows.kt",
                mustacheTemplate("workflows/flow.mustache")
            ),
            generateFromTemplate(
                "workflows/src/test/kotlin/${packageName.replace(".", "/")}",
                "FlowTests.kt",
                mustacheTemplate("workflows/flow_test.mustache")
            ),
            generateFromTemplate(
                "workflows/src/test/kotlin/${packageName.replace(".", "/")}",
                "ContractTests.kt",
                mustacheTemplate("workflows/flow_contract_test.mustache")
            ),
            generateFromTemplate(
                "workflows",
                "build.gradle",
                mustacheTemplate("workflows/gradle_build.mustache")
            ),
            generateFromTemplate(
                "workflows/src/test/kotlin/${packageName.replace(".", "/")}",
                "NodeDriver.kt",
                mustacheTemplate("workflows/node_driver.mustache")
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

    companion object {
        private const val TEMPLATE_DIR = "cordapp"

        private val compiler = Mustache.compiler()
    }
}
