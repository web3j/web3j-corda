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
package org.web3j.corda.util

enum class OpenApiVersion(val version: String) {

    v2_0("2.0"),
    v3_0("3.0"),
    v3_0_1("3.0.1");

    override fun toString() = version

    fun toInt() = version.split(".").first().toInt()

    companion object {

        @JvmStatic
        fun fromVersion(version: String): OpenApiVersion {
            return values().find { it.version == version }
                ?: throw IllegalArgumentException("No enum constant for version $version")
        }
    }
}
