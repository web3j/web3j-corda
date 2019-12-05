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
@file:JvmName("CordaUtils")

package org.web3j.corda.util

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.ServerSocket
import java.util.Optional
import javax.naming.ldap.LdapName
import javax.security.auth.x500.X500Principal

fun Any.toJson(): String = mapper
    .writerWithDefaultPrettyPrinter()
    .writeValueAsString(this)

val mapper: ObjectMapper = jacksonObjectMapper()
    .setDefaultSetterInfo(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY))
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
    .enable(SerializationFeature.INDENT_OUTPUT)

fun <T> convert(value: Any, type: Class<T>): T = mapper.convertValue(value, type)

inline fun <reified T : Any> Any.convert() = mapper.convertValue<T>(this)

val isMac = System.getProperty("os.name").contains("Mac", true)

val X500Principal.canonicalName: String
    get() = getName(X500Principal.CANONICAL).run {
        LdapName(this).rdns.toMutableList().apply { reverse() }
            .joinToString("-") { it.value.toString() }
            .replace(' ', '-')
    }

val regexToReplace = "-[0-9][^/]*".toRegex()

fun sanitizeCorDappName(name: String): String {
    return name.replace(regexToReplace, "")
}

fun needToSanitizeCorDappName(name: String): Boolean {
    return name.contains(regexToReplace)
}

fun randomPort() = ServerSocket(0).use { it.localPort }

fun <T> T?.toOptional(): Optional<T> = Optional.ofNullable(this)
