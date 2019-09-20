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
@file:JvmName("CordaUtils")

package org.web3j.corda.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun Any.toJson(): String = mapper
    .writerWithDefaultPrettyPrinter()
    .writeValueAsString(this)

val mapper: ObjectMapper = jacksonObjectMapper()
    .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
    .enable(SerializationFeature.INDENT_OUTPUT)

fun <T> convert(value: Any, type: Class<T>): T = mapper.convertValue(value, type)

inline fun <reified T : Any> Any.convert() = mapper.convertValue<T>(this)

fun <K, V> Iterable<Pair<K, V>>.toNonNullMap(): NonNullMap<K, V> = NonNullMap(toMap())

class NonNullMap<K, V>(private val map: Map<K, V>) : Map<K, V> by map {
    override operator fun get(key: K): V {
        return map[key] ?: error("Key not found: $key")
    }
}

val isMac = System.getProperty("os.name").contains("Mac", true)
