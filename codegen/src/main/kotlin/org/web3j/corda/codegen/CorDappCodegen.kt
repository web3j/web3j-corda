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
import org.openapitools.codegen.templating.mustache.CamelCaseLambda
import org.openapitools.codegen.templating.mustache.LowercaseLambda
import org.openapitools.codegen.templating.mustache.TitlecaseLambda
import org.openapitools.codegen.utils.StringUtils.camelize
import org.web3j.corda.model.AbstractParty
import org.web3j.corda.model.Amount
import org.web3j.corda.model.AmountCurrency
import org.web3j.corda.model.AttachmentConstraint
import org.web3j.corda.model.Command
import org.web3j.corda.model.CommandObject
import org.web3j.corda.model.ComponentGroup
import org.web3j.corda.model.Constraint
import org.web3j.corda.model.ContractState
import org.web3j.corda.model.CoreTransaction
import org.web3j.corda.model.Data
import org.web3j.corda.model.LinearId
import org.web3j.corda.model.LoginRequest
import org.web3j.corda.model.MerkleTree
import org.web3j.corda.model.NetworkHostAndPort
import org.web3j.corda.model.NotaryChangeWireTransaction
import org.web3j.corda.model.NotaryType
import org.web3j.corda.model.Output
import org.web3j.corda.model.Party
import org.web3j.corda.model.Result
import org.web3j.corda.model.SignedTransaction
import org.web3j.corda.model.SimpleNodeInfo
import org.web3j.corda.model.StateAndRef
import org.web3j.corda.model.StateAndRefObject
import org.web3j.corda.model.StateRef
import org.web3j.corda.model.TimeWindow
import org.web3j.corda.model.TransactionStateContractState
import org.web3j.corda.model.TransactionStateObject
import org.web3j.corda.model.WireTransaction
import java.io.File

class CorDappCodegen(
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
        apiTemplateFiles["cordapp.mustache"] = ".kt"
        apiTestTemplateFiles["cordapp_test.mustache"] = ".kt"
        templateDir = TEMPLATE_DIR
        embeddedTemplateDir = TEMPLATE_DIR
        modelPackage = "$packageName.model"
        allowUnicodeIdentifiers = true
    }

    override fun processOpts() {
        super.processOpts()

        additionalProperties["java8"] = true
        additionalProperties["lowercase"] = LowercaseLambda()
        additionalProperties["camelcase"] = CamelCaseLambda()
        additionalProperties["titlecase"] = TitlecaseLambda()

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
    override fun toApiName(name: String): String = camelize(name)

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
        return super.postProcessOperationsWithModels(objs, allModels)
    }

    companion object {
        const val TEMPLATE_DIR = "cordapp"

        internal val CORDA_SERIALIZABLE = setOf(
            AbstractParty::class,
            Amount::class,
            AmountCurrency::class,
            AttachmentConstraint::class,
            CommandObject::class,
            Command::class,
            ComponentGroup::class,
            Constraint::class,
            ContractState::class,
            CoreTransaction::class,
            Data::class,
            LinearId::class,
            LoginRequest::class,
            MerkleTree::class,
            NetworkHostAndPort::class,
            NotaryChangeWireTransaction::class,
            NotaryType::class,
            Output::class,
            Party::class,
            Result::class,
            SignedTransaction::class,
            SimpleNodeInfo::class,
            StateAndRef::class,
            StateAndRefObject::class,
            StateRef::class,
            TimeWindow::class,
            TransactionStateContractState::class,
            TransactionStateObject::class,
            WireTransaction::class
        )

        private fun buildApiPackage(apiPackage: String, pathPrefix: String) =
            "$apiPackage.${pathPrefix.replace("-", ".")}.api"

        private fun buildFlowId(path: String) = path.split("/".toRegex())[4]
            .split("\\.".toRegex())
            .last()
            .replace("$", "")

        private fun buildFlowPath(path: String) = path.split("/".toRegex())[4]
            .replace("$", "\\$")
    }
}
