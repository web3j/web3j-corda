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

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import org.gradle.tooling.GradleConnector
import org.web3j.corda.codegen.CorDappGenerator
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Option
import picocli.CommandLine.Spec

/**
 * Custom CLI interpreter to generate a new sample CordApp and web3j client.
 */
@Command(
    name = "new",
    description = ["Create an empty CorDapp project with its client wrappers."]
)
class NewCommand : BaseCommand() {

    @Spec
    private lateinit var spec: CommandSpec

    @Option(
        names = ["-n", "--name"],
        description = ["CordApp name"],
        required = true
    )
    lateinit var corDappName: String

    override fun run() {
        // Generate CorDapp project
        CorDappGenerator(
            packageName = packageName,
            corDappName = corDappName,
            outputDir = outputDir,
            version = spec.parent().versionProvider().version.first()
        ).generate()

        copyProjectResources()

        // Build CorDapp JAR for the client
        runGradleBuild(outputDir.toPath())

        // Generate the CorDapp client classes
        GenerateCommand().apply {
            cordaResource = CordaResource().apply {
                // should be individual contracts and workflows to be copied
                corDappsDir = this@NewCommand.outputDir
            }
            packageName = this@NewCommand.packageName
            outputDir = File("${this@NewCommand.outputDir}${File.separator}clients")
            generateTests = false
            run()
        }
        println("Sample CorDapp created with name: $corDappName at location: $outputDir")
    }

    private fun runGradleBuild(projectRoot: Path) {
        GradleConnector.newConnector().apply {
            useBuildDistribution()
            forProjectDirectory(projectRoot.toFile())
        }.connect().use {
            it.newBuild().apply {
                forTasks("jar")
                run()
            }
        }
    }

    private fun copyProjectResources() {
        copyResource("settings.gradle", outputDir)
        copyResource("gradlew.bat", outputDir)
        copyResource("gradlew", outputDir)

        File("${outputDir.toURI().path}${File.separator}gradlew").setExecutable(true)

        val gradleFolder = File("$outputDir${File.separator}gradle${File.separator}wrapper").apply { mkdirs() }
        copyResource("gradle-wrapper.jar", gradleFolder)
        copyResource("gradle-wrapper.properties", gradleFolder)
        copyResource("README.md", outputDir)
    }

    private fun copyResource(name: String, outputDir: File) {
        Files.copy(
            javaClass.classLoader.getResource(name)?.openStream()!!,
            outputDir.resolve(name).toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
    }
}
