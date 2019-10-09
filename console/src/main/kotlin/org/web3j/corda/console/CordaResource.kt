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

import picocli.CommandLine.ITypeConverter
import picocli.CommandLine.Option
import java.io.File
import java.net.MalformedURLException
import java.net.URL

class CordaResource {

    @Option(
        names = ["-u", "--url"],
        description = ["Corda node OpenAPI URL"],
        converter = [URLConverter::class],
        required = true
    )
    lateinit var openApiUrl: URL

    @Option(
        names = ["-d", "--cordapps-dir"],
        description = ["CorDapps node directory"],
        required = true
    )
    lateinit var corDappsDir: File

    fun isCorDappsDirInitialized(): Boolean = ::corDappsDir.isInitialized

    private class URLConverter : ITypeConverter<URL> {
        override fun convert(value: String): URL {
            return try {
                URL(value)
            } catch (e: MalformedURLException) {
                URL("file:$value")
            }
        }
    }
}
