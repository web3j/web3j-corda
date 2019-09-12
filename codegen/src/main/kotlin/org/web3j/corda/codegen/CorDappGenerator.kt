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
    private val outputDir: File
) : CordaGenerator {

    private val context = mutableMapOf<String, Any>()

    override fun generate(): List<File> {
        context["packageName"] = packageName
        context["corDappName"] = corDappName
        CordaGeneratorUtils.addLambdas(context)

        return generateTemplateFlow() + generateTemplateContract()
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
                mustacheTemplate("contract_test.mustache")
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
                mustacheTemplate("contract_test.mustache")
            ),
            generateFromTemplate(
                "workflows",
                "build.gradle",
                mustacheTemplate("workflows/gradle_build.mustache")
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
