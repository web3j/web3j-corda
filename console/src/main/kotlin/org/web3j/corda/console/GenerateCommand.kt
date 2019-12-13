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
package org.web3j.corda.console

import io.bluebank.braid.corda.server.BraidDocsMain
import io.bluebank.braid.core.utils.toJarsClassLoader
import io.bluebank.braid.core.utils.tryWithClassLoader
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import kotlin.streams.toList
import net.corda.core.internal.walk
import org.web3j.corda.codegen.CorDappClientGenerator
import org.web3j.corda.util.OpenApiVersion
import org.web3j.corda.util.OpenApiVersion.v3_0_1
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.Command
import picocli.CommandLine.ITypeConverter
import picocli.CommandLine.Option

/**
 * Custom CLI interpreter to generate a new template web3j wrappers for given CordApp.
 */
@Command(
    name = "generate",
    description = ["Create client wrapper classes for existing CorDapps."]
)
class GenerateCommand : BaseCommand() {

    @ArgGroup(exclusive = true, multiplicity = "1")
    lateinit var cordaResource: CordaResource

    @Option(
        names = ["-v", "--open-api-version"],
        description = ["OpenAPI version: \${COMPLETION-CANDIDATES} (default \${DEFAULT-VALUE})"],
        converter = [OpenApiVersionConverter::class],
        required = false
    )
    var openApiVersion = v3_0_1

    @Option(
        names = ["-t", "--generate-tests"],
        description = ["Generate client stubbed tests"],
        required = false
    )
    var generateTests = true

    override fun run() {
        if (cordaResource.isCorDappsDirInitialized()) {
            generateOpenApiDef()
        } else {
            fetchOpenApiDef()
        }.apply {
            CorDappClientGenerator(
                packageName,
                this,
                outputDir,
                generateTests
            ).generate()
        }
        println("CorDapp client generated at location: $outputDir")
    }

    private fun fetchOpenApiDef(): String {
        return cordaResource.openApiUrl.toURI().toURL().run {
            if (!toExternalForm().endsWith(".json")) {
                URL("$this/swagger.json")
            } else {
                this
            }
        }.openStream().readBytes().toString(UTF_8)
    }

    private fun generateOpenApiDef(): String {
        return cordaResource.corDappsDir.toPath().walk { paths ->
            paths.map {
                it.toFile().toURI().toURL().toExternalForm()
            }.filter {
                it.endsWith(".jar") &&
                    !it.endsWith("quasar.jar") &&
                    !it.endsWith("gradle-wrapper.jar")
            }.toList().run {
                tryWithClassLoader(toJarsClassLoader()) {
                    BraidDocsMain().swaggerText(openApiVersion.toInt())
                }
            }
        }
    }

    private class OpenApiVersionConverter : ITypeConverter<OpenApiVersion> {
        override fun convert(value: String) = OpenApiVersion.fromVersion(value)
    }
}
