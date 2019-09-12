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

import io.swagger.parser.OpenAPIParser
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult
import org.openapitools.codegen.ClientOptInput
import org.openapitools.codegen.ClientOpts
import org.openapitools.codegen.CodegenConstants.APIS
import org.openapitools.codegen.CodegenConstants.API_TESTS
import org.openapitools.codegen.CodegenConstants.MODELS
import org.openapitools.codegen.DefaultGenerator
import org.openapitools.codegen.config.GeneratorProperties.setProperty
import java.io.File

class CorDappClientGenerator(
    private val packageName: String,
    private val openApiDef: String,
    private val outputDir: File,
    private val generateTests: Boolean
) : DefaultGenerator(), CordaGenerator {

    override fun generate(): List<File> {
        val codegen = CorDappClientCodegen(packageName, outputDir).apply {
            // TODO setArtifactId("web3j-corda")
        }

        // Filter common API endpoints
        val result = parser.readContents(openApiDef, listOf(), parseOptions)
        result.openAPI.paths.entries.removeIf {
            !it.key.startsWith("/cordapps") || it.key.endsWith("/flows")
        }

        opts(
            ClientOptInput()
                .config(codegen)
                .opts(ClientOpts())
                .openAPI(result.openAPI)
        )
        configureGeneratorProperties(result)
        setGenerateMetadata(false)

        return super.generate().onEach {
            CordaGeneratorUtils.kotlinFormat(it)
        }
    }

    private fun configureGeneratorProperties(result: SwaggerParseResult) {
        val cordaTypes = CorDappClientCodegen.CORDA_SERIALIZABLES.map {
            it.simpleName
        }
        val models = result.openAPI.components.schemas.keys.filter {
            !cordaTypes.contains(it)
        }
        // Specify the list of model classes to generate
        setProperty(MODELS, models.joinToString(separator = ","))
        setProperty(APIS, result.openAPI.paths.keys.joinToString(separator = ",") {
            buildCorDappNameFromPath(it)
        })
        setProperty(API_TESTS, generateTests.toString())
    }

    companion object {
        private val parser = OpenAPIParser()
        private val parseOptions = ParseOptions()

        fun buildCorDappNameFromPath(path: String): String {
            return (path.split("/".toRegex())[2])
        }
    }
}
