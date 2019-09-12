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

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfoList
import io.swagger.v3.oas.models.Operation
import org.openapitools.codegen.CodegenConstants
import org.openapitools.codegen.CodegenOperation
import org.openapitools.codegen.languages.AbstractKotlinCodegen
import org.openapitools.codegen.utils.StringUtils.camelize
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

class CorDappClientCodegen(
    packageName: String,
    outputDir: File
) : AbstractKotlinCodegen() {

    private val cordaMapping = mutableMapOf<String, String>()

    init {
        this.packageName = packageName

        // cliOptions default redefinition need to be updated
        updateOption(CodegenConstants.ARTIFACT_ID, artifactId)
        updateOption(CodegenConstants.PACKAGE_NAME, packageName)

        outputFolder = outputDir.absolutePath
        modelTemplateFiles["model.mustache"] = ".kt"
        apiTemplateFiles["cordapp_client.mustache"] = ".kt"
        apiTestTemplateFiles["cordapp_client_test.mustache"] = ".kt"
        templateDir = TEMPLATE_DIR
        embeddedTemplateDir = TEMPLATE_DIR
        modelPackage = "$packageName.model"
        allowUnicodeIdentifiers = true
    }

    override fun processOpts() {
        super.processOpts()

        additionalProperties["java8"] = true
        CordaGeneratorUtils.addLambdas(additionalProperties)

        typeMapping["array"] = "kotlin.collections.List"
        typeMapping["list"] = "kotlin.collections.List"

        // Add Corda type mappings
        CORDA_SERIALIZABLES.forEach {
            cordaMapping[it.simpleName] = it.name
        }
    }

    override fun toModelImport(name: String): String? {
        return when {
            modelPackage().isEmpty() -> name
            cordaMapping.containsKey(name) -> cordaMapping[name]
            needToImport(name) -> return super.toModelImport(name)
            else -> "$modelPackage.$name"
        }
    }

    override fun getOrGenerateOperationId(
        operation: Operation,
        path: String,
        httpMethod: String
    ): String {
        return if (operation.operationId.isNullOrEmpty()) {
            camelize(path.split("/".toRegex()).last())
        } else {
            operation.operationId
        }
    }

    /**
     * Do not append the `Api` suffix on the CorDapp interface.
     */
    override fun toApiName(name: String): String = camelize(name)

    override fun addOperationToGroup(
        tag: String,
        resourcePath: String,
        operation: Operation,
        codegenOperation: CodegenOperation,
        operations: Map<String, List<CodegenOperation>>
    ) {
        val path = CorDappClientGenerator.buildCorDappNameFromPath(codegenOperation.path)
        super.addOperationToGroup(path, resourcePath, operation, codegenOperation, operations)
    }

    override fun postProcessOperationsWithModels(
        objs: MutableMap<String, Any>,
        allModels: List<Any>
    ): Map<String, Any> {
        val operation = (objs["operations"] as Map<*, *>)["operation"] as List<*>
        val flows = operation.filterIsInstance<CodegenOperation>()

        ((objs["operations"] as Map<*, *>)["pathPrefix"] as String).run {
            apiPackage = buildApiPackage(packageName, this)
            additionalProperties.put(CodegenConstants.API_PACKAGE, apiPackage)
        }
        objs["flows"] = flows.map {
            mutableMapOf<String, String>(
                "flowPath" to buildFlowPath(it.path),
                "flowId" to buildFlowId(it.path),
                "outputClass" to if (it.returnType.isNullOrBlank()) "Unit" else it.returnType,
                "inputClass" to it.bodyParams[0].baseType
            ).apply {
                if (it.hasConsumes) {
                    (it.consumes[0] as Map<String, String>)["mediaType"]?.let { mediaType ->
                        put("consumes", mediaType)
                    }
                }
                if (it.hasProduces) {
                    (it.produces[0] as Map<String, String>)["mediaType"]?.let { mediaType ->
                        put("produces", mediaType)
                    }
                }
            }
        }
        objs["currentDate"] = OffsetDateTime.now(ZoneOffset.UTC).format(ISO_DATE_TIME)
        return super.postProcessOperationsWithModels(objs, allModels)
    }

    companion object {
        const val TEMPLATE_DIR = "cordapp-client"

        // Load model class dynamically to avoid re-generation
        internal val CORDA_SERIALIZABLES: ClassInfoList by lazy {
            ClassGraph().enableClassInfo().scan().allClasses.filter {
                it.packageName == "org.web3j.corda.model"
            }
        }

        private fun buildApiPackage(apiPackage: String, pathPrefix: String): String {
            val pathPackage = pathPrefix.split("-").filterNot {
                // Remove overlapping sections between package and path
                // to avoid cases like 'org.web3j.corda.corda.core'
                apiPackage.endsWith(it)
            }.joinToString(separator = ".")

            return "$apiPackage.$pathPackage.api"
        }

        private fun buildFlowId(path: String) = path.split("/".toRegex())[4]
            .split("\\.".toRegex())
            .last()
            .replace("$", "")

        private fun buildFlowPath(path: String) = path.split("/".toRegex())[4]
            .replace("$", "\\$")
    }
}
