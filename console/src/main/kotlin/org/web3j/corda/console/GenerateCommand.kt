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

import io.bluebank.braid.server.BraidDocsMain
import net.corda.core.internal.list
import org.web3j.corda.codegen.CorDappGenerator
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.charset.StandardCharsets

/**
 * Custom CLI interpreter to generate a new template web3j wrappers for given CordApp.
 */
@Command(name = "generate")
class GenerateCommand : CommonCommand() {

    @ArgGroup(exclusive = true, multiplicity = "1")
    lateinit var cordaResource: CordaResource

    object CordaResource {

        @Option(
            names = ["-u", "--url"],
            description = ["Corda node OpenAPI URL"],
            required = true
        )
        lateinit var openApiUrl: URL

        @Option(
            names = ["-d", "--cordappsDir"],
            description = ["CorDapps node directory"],
            required = true
        )
        lateinit var corDappsDir: File

        val isCorDappsDirInitialized: Boolean by lazy {
            ::corDappsDir.isInitialized
        }
    }

    override fun run() {
        if (cordaResource.isCorDappsDirInitialized) {
            generateOpenApiDef(cordaResource.corDappsDir)
        } else {
            fetchOpenApiDef(cordaResource.openApiUrl)
        }.apply {
            CorDappGenerator(packageName, this, outputDir).generate()
        }
    }

    companion object {
        private fun fetchOpenApiDef(url: URL): String {
            return url.run {
                if (!toExternalForm().endsWith(".json")) {
                    URL("$url/swagger.json")
                } else {
                    this
                }
            }.openStream().readBytes().toString(StandardCharsets.UTF_8)
        }

        private fun generateOpenApiDef(file: File): String {
            return file.toPath().list().map {
                it.toFile().toURI().toURL()
            }.run {
                BraidDocsMain(
                    URLClassLoader(
                        toTypedArray(),
                        BraidDocsMain::class.java.classLoader
                    )
                ).swaggerText()
            }
        }
    }
}
