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

import java.lang.IllegalStateException
import java.util.Properties
import org.web3j.corda.console.CordaCommand.VersionProvider
import picocli.CommandLine.Command
import picocli.CommandLine.IVersionProvider
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.ParameterException
import picocli.CommandLine.Spec

/**
 * Custom CLI interpreter for Corda applications.
 */
@Command(
    name = "web3j-corda",
    description = ["Web3j command-line tools for the Corda blockchain platform."],
    sortOptions = false,
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider::class,
    subcommands = [
        GenerateCommand::class,
        NewCommand::class
    ]
)
class CordaCommand : Runnable {

    @Spec
    private lateinit var spec: CommandSpec

    override fun run() {
        throw ParameterException(spec.commandLine(), "Missing required sub-command (see below)")
    }

    internal object VersionProvider : IVersionProvider {
        override fun getVersion(): Array<String> {
            val url = javaClass.classLoader.getResource("version.properties")
                ?: throw IllegalStateException("No version.properties file found in the classpath.")
            return arrayOf(Properties().apply {
                load(url.openStream())
            }.getProperty("version"))
        }
    }
}
