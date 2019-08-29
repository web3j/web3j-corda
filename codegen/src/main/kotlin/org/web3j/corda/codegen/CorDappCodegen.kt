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

import io.swagger.v3.oas.models.Operation
import org.openapitools.codegen.CodegenConstants
import org.openapitools.codegen.CodegenOperation
import org.openapitools.codegen.languages.AbstractKotlinCodegen
import org.openapitools.codegen.templating.mustache.LowercaseLambda
import org.openapitools.codegen.utils.StringUtils.camelize
import org.web3j.corda.model.Commands
import org.web3j.corda.model.ComponentGroup
import org.web3j.corda.model.Constraint
import org.web3j.corda.model.CordaX500Name
import org.web3j.corda.model.CoreTransaction
import org.web3j.corda.model.Data
import org.web3j.corda.model.LinearId
import org.web3j.corda.model.Money
import org.web3j.corda.model.NetworkHostAndPort
import org.web3j.corda.model.Output
import org.web3j.corda.model.Party
import org.web3j.corda.model.PublicKey
import org.web3j.corda.model.SignedTransaction
import org.web3j.corda.model.SimpleNodeInfo
import org.web3j.corda.model.TimeWindow
import org.web3j.corda.model.Value

class CorDappCodegen(
    artifactId: String,
    packageName: String,
    outputDir: String
) : AbstractKotlinCodegen() {

    private val cordaMapping = mutableMapOf<String, String>()

    init {
        this.artifactId = artifactId
        this.packageName = packageName

        // cliOptions default redefinition need to be updated
        updateOption(CodegenConstants.ARTIFACT_ID, artifactId)
        updateOption(CodegenConstants.PACKAGE_NAME, packageName)

        outputFolder = outputDir
        modelTemplateFiles["model.mustache"] = ".kt"
        apiTemplateFiles["cordapp.mustache"] = ".kt"
        templateDir = TEMPLATE_DIR
        embeddedTemplateDir = TEMPLATE_DIR
//        templatingEngine = TEMPLATE_ENGINE
        apiPackage = "$packageName.api"
        modelPackage = "$packageName.model"
        allowUnicodeIdentifiers = true
    }

    override fun processOpts() {
        super.processOpts()

        additionalProperties["java8"] = true
        additionalProperties["lowercase"] = LowercaseLambda()

        typeMapping["array"] = "kotlin.collections.List"
        typeMapping["list"] = "kotlin.collections.List"

        // Add Corda type mappings
        CORDA_SERIALIZABLE.forEach {
            cordaMapping[it.simpleName!!] = it.qualifiedName!!
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
    override fun toApiName(name: String): String = name

    override fun addOperationToGroup(
        tag: String,
        resourcePath: String,
        operation: Operation,
        co: CodegenOperation,
        operations: Map<String, List<CodegenOperation>>
    ) {
        val path = CorDappGenerator.buildCorDappNameFromPath(co.path)
        super.addOperationToGroup(path, resourcePath, operation, co, operations)
    }

    override fun postProcessOperationsWithModels(
        objs: MutableMap<String, Any>,
        allModels: MutableList<Any>
    ): MutableMap<String, Any> {

        val operation = (objs["operations"] as HashMap<*, *>)["operation"] as ArrayList<*>
        val flows = operation.filterIsInstance<CodegenOperation>()

        objs["flows"] = flows.map {
            hashMapOf<String, String>(
                "flowId" to buildFlowNameFromPath(it.path),
                    "outputClass" to it.returnType,
                    "inputClass" to it.bodyParams[0].baseType,
                    "consumes" to (it.consumes[0] as HashMap)["mediaType"]!!,
                    "produces" to (it.produces[0] as HashMap)["mediaType"]!!)
        }
        return super.postProcessOperationsWithModels(objs, allModels)
    }

    companion object {
        const val TEMPLATE_DIR = "cordapp"

        internal val CORDA_SERIALIZABLE = setOf(
                SignedTransaction::class,
                NetworkHostAndPort::class,
                SimpleNodeInfo::class,
                CoreTransaction::class,
                TimeWindow::class,
                Commands::class,
                Party::class,
                Value::class,
                ComponentGroup::class,
                Output::class,
                Constraint::class,
                Data::class,
                LinearId::class,
                Money::class,
                CordaX500Name::class,
                PublicKey::class
        )

        private fun buildFlowNameFromPath(path: String): String {
            return camelize(path.split("/".toRegex())[4].split("-".toRegex())[0])
        }
    }
}
