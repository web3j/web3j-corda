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

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.ruleset.experimental.ExperimentalRuleSetProvider
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult
import mu.KLogging
import org.openapitools.codegen.ClientOptInput
import org.openapitools.codegen.ClientOpts
import org.openapitools.codegen.CodegenConstants
import org.openapitools.codegen.DefaultGenerator
import org.openapitools.codegen.config.GeneratorProperties.setProperty
import java.io.File

class CorDappGenerator(
    private val packageName: String,
    private val openApiDef: String,
    private val outputDir: File
) : DefaultGenerator() {

    override fun generate(): List<File> {

        val codegen = CorDappCodegen(packageName, outputDir)
        val result = parser.readContents(openApiDef, listOf(), parseOptions)

        // Filter common API endpoints
        result.openAPI.paths.entries.removeIf {
            !it.key.startsWith("/cordapps") || it.key.endsWith("/flows")
        }

        opts(
                ClientOptInput()
                        .config(codegen)
                        .opts(ClientOpts().apply { properties })
                        .openAPI(result.openAPI)
        )
        configureGeneratorProperties(result)
        setGenerateMetadata(false)

        return super.generate().also {
            it.forEach { file ->
                // Format generated code using KtLint
                KtLint.format(KtLint.Params(
                        ruleSets = ruleSets,
                        cb = { error, _ ->
                            logger.warn { error }
                        },
                        text = file.readText(),
                        debug = true
                )).run {
                    file.writeText(this)
                }
            }
        }
    }

    private fun configureGeneratorProperties(result: SwaggerParseResult) {
        val cordaTypes = CorDappCodegen.CORDA_SERIALIZABLE.map {
            it.simpleName
        }

        val models = result.openAPI.components.schemas.keys.filter {
            !cordaTypes.contains(it)
        }

        // Specify the list of model classes to generate
        setProperty(CodegenConstants.MODELS, models.joinToString(separator = ","))
        setProperty(CodegenConstants.APIS, result.openAPI.paths.keys.joinToString(separator = ",") {
            buildCorDappNameFromPath(it)
        })
    }

    companion object : KLogging() {
        private val parser = OpenAPIParser()
        private val parseOptions = ParseOptions()

        private val ruleSets = listOf(
                StandardRuleSetProvider().get(),
                ExperimentalRuleSetProvider().get()
        )

        fun buildCorDappNameFromPath(path: String): String {
            return (path.split("/".toRegex())[2])
        }
    }
}
