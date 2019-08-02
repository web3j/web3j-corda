package org.web3j.corda.util

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun Any.toJson(): String = mapper
    .writerWithDefaultPrettyPrinter()
    .writeValueAsString(this)

@PublishedApi
internal val mapper = jacksonObjectMapper()

fun <T> convert(value: Any, type: Class<T>): T = mapper.convertValue(value, type)

inline fun <reified T> convert(value: Any) = mapper.convertValue<T>(value)
