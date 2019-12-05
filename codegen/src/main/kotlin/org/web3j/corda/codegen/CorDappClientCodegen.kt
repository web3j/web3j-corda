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

import io.swagger.v3.oas.models.Operation
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import org.openapitools.codegen.CodegenConstants
import org.openapitools.codegen.CodegenConstants.API_PACKAGE
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.CodegenOperation
import org.openapitools.codegen.languages.AbstractKotlinCodegen
import org.openapitools.codegen.utils.StringUtils.camelize
import org.web3j.corda.codegen.CordaGeneratorUtils.addLambdas
import org.web3j.corda.codegen.CordaGeneratorUtils.repackage

class CorDappClientCodegen(
    packageName: String,
    outputDir: File,
    typeMapping: Map<String, String>,
    private val cordaMapping: Map<String, String>
) : AbstractKotlinCodegen() {

    init {
        this.packageName = packageName
        this.typeMapping.putAll(typeMapping)

        // cliOptions default redefinition need to be updated
        updateOption(CodegenConstants.ARTIFACT_ID, artifactId)
        updateOption(CodegenConstants.PACKAGE_NAME, packageName)

        outputFolder = outputDir.absolutePath
        modelTemplateFiles["model.mustache"] = ".kt"
        apiTemplateFiles["cordapp_client.mustache"] = ".kt"
        apiTestTemplateFiles["cordapp_client_test.mustache"] = ".kt"
        templateDir = TEMPLATE_DIR
        embeddedTemplateDir = TEMPLATE_DIR
        allowUnicodeIdentifiers = true
    }

    override fun processOpts() {
        super.processOpts()

        additionalProperties["java8"] = true
        addLambdas(additionalProperties)

        // Kotlin native types
        typeMapping["array"] = "kotlin.collections.List"
        typeMapping["list"] = "kotlin.collections.List"
    }

    /**
     * Force the order of imports if specified in the given mapping.
     */
    override fun toModelImport(name: String): String {
        return when {
            typeMapping.containsKey(name) -> typeMapping[name]!!
            needToImport(name) -> super.toModelImport(name)
            modelPackage().isEmpty() -> repackage(name, cordaMapping)
            else -> "${modelPackage()}.$name"
        }
    }

    /**
     * Update the model name to incorporate the package name when specified.
     */
    override fun toModelName(name: String): String {
        return when {
            importMapping.containsKey(name) -> importMapping[name]!!
            else -> repackage(name, cordaMapping)
        }
    }

    /**
     * Create folder structure according to the given package structure.
     */
    override fun toModelFilename(name: String): String {
        return repackage(name, cordaMapping).replace(".", File.separator)
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

    override fun postProcessAllModels(objs: MutableMap<String, Any>): MutableMap<String, Any> {
        objs.values.forEach {
            @Suppress("UNCHECKED_CAST")
            val modelContext = it as MutableMap<String, Any>
            updateImportsPackage(modelContext)
            updateCommonContext(modelContext)
        }
        return super.postProcessAllModels(updateModelsPackage(objs))
    }

    override fun postProcessModels(objs: MutableMap<String, Any>): MutableMap<String, Any> {
        @Suppress("UNCHECKED_CAST")
        val nameProp = (((objs["models"] as MutableList<Any>)
            .first() as MutableMap<String, Any>)["model"] as CodegenModel)::name

        splitPackageFromClass(nameProp.get()).apply {
            objs["package"] = first ?: packageName
            nameProp.set(second)
        }
        return super.postProcessModels(objs)
    }

    override fun postProcessOperationsWithModels(
        objs: MutableMap<String, Any>,
        allModels: List<Any>
    ): Map<String, Any> {
        val operation = (objs["operations"] as Map<*, *>)["operation"] as List<*>
        val flows = operation.filterIsInstance<CodegenOperation>()

        ((objs["operations"] as Map<*, *>)["pathPrefix"] as String).run {
            apiPackage = buildApiPackage(packageName, this)
            additionalProperties[API_PACKAGE] = apiPackage
        }
        objs["flows"] = flows.map {
            mutableMapOf<String, Any>(
                "flowPath" to buildFlowPath(it.path),
                "flowId" to buildFlowId(it.path),
                "hasOutput" to it.hasOutput,
                "outputClass" to it.returnType,
                "hasInput" to it.hasInput,
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
        updateCommonContext(objs)
        return super.postProcessOperationsWithModels(objs, allModels)
    }

    /**
     * Prepend the package name to the imports for schemas with an empty package.
     */
    @Suppress("UNCHECKED_CAST")
    private fun updateImportsPackage(modelContext: MutableMap<String, Any>) {
        (modelContext["imports"] as MutableList<Any>).forEach {
            val import = it as MutableMap<String, String>
            if (!import["import"]!!.contains(".") && packageName.isNotBlank()) {
                import.replace("import", "$packageName.${import["import"]}")
            }
        }
    }

    /**
     * Prepend the package name to all schemas with an empty package.
     */
    private fun updateModelsPackage(objs: MutableMap<String, Any>): MutableMap<String, Any> {
        return objs.map {
            if (!it.key.contains(".") && packageName.isNotBlank()) {
                "$packageName.${it.key}" to it.value
            } else {
                it.key to it.value
            }
        }.toMap().toMutableMap()
    }

    /**
     * Add required context variables to any context, model or API.
     */
    private fun updateCommonContext(context: MutableMap<String, Any>) {
        context["currentDate"] = OffsetDateTime.now(UTC).format(ISO_DATE_TIME)
        context["generator"] = CorDappClientGenerator::class.qualifiedName!!
    }

    companion object {

        /**
         * Directory for Mustache templates.
         */
        private const val TEMPLATE_DIR = "cordapp/client"

        /**
         * Separate the package name and class name
         */
        private fun splitPackageFromClass(name: String): Pair<String?, String> {
            val lastDotIndex = name.lastIndexOf('.')
            return if (0 < lastDotIndex) {
                val packageName = name.substring(0 until lastDotIndex)
                val simpleName = name.substring(lastDotIndex + 1)
                packageName to simpleName
            } else {
                null to name
            }
        }

        private fun buildApiPackage(apiPackage: String, pathPrefix: String): String {
            val pathPackage = pathPrefix.split("-").filterNot {
                // Remove overlapping sections between package and path
                // to avoid cases like 'org.web3j.corda.corda.core'
                apiPackage.endsWith(it)
            }.joinToString(separator = ".")

            return "$apiPackage${if (pathPackage.isNotBlank()) ".$pathPackage" else ""}.api"
        }

        private fun buildFlowId(path: String) = path.split("/".toRegex())[4]
            .split("\\.".toRegex())
            .last()
            .replace("$", "_")

        private fun buildFlowPath(path: String) = path.split("/".toRegex())[4]
            .replace("$", "\\$")

        private val CodegenOperation.hasOutput: Boolean
            get() = returnType != null

        private val CodegenOperation.hasInput: Boolean
            get() = bodyParams[0].baseType != Any::class.qualifiedName
    }
}
