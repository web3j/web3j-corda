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
package org.web3j.corda.assertion

import assertk.Assert
import assertk.all
import assertk.assertions.support.expected
import assertk.assertions.support.show
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaType

/**
 * Asserts the class has the expected qualified name.
 */
fun Assert<KClass<*>>.hasName(name: String) = given { actual ->
    if (actual.qualifiedName != name) {
        expected("class name:${show(name)} but was:${show(actual.qualifiedName)}")
    }
}

/**
 * Asserts the class has the expected declared inner class.
 */
fun Assert<KClass<*>>.hasDeclaredMember(name: String) = given { actual ->
    actual.declaredMembers.map { it.name }.also {
        if (!it.contains(name)) {
            expected("declared member:${show(name)} but was:${show(it)}")
        }
    }
}

/**
 * Asserts the class has the expected declared inner class.
 */
fun Assert<KClass<*>>.hasNestedClass(qualifiedName: String) = given { actual ->
    actual.nestedClasses.map { it.qualifiedName }.also {
        if (!it.contains(qualifiedName)) {
            expected("nested class:${show(qualifiedName)} but was:${show(it)}")
        }
    }
}

/**
 * Asserts the class has the expected declared function.
 */
fun Assert<KClass<*>>.hasFunction(name: String, returnType: String, vararg parameterTypes: String) = given { actual ->
    actual.memberFunctions.also { functions ->
        if (!functions.map { it.name }.contains(name)) {
            expected("function:${show(name)} but was:${show(functions)}")
        }
        assertThat(functions.first { it.name == name }).all {
            hasParameters(*parameterTypes)
            returns(returnType)
        }
    }
}

/**
 * Asserts the class has the expected declared void function.
 */
fun Assert<KClass<*>>.hasVoidFunction(name: String, vararg parameterTypes: String) = given { actual ->
    actual.memberFunctions.also { functions ->
        if (!functions.map { it.name }.contains(name)) {
            expected("class:${show(actual.qualifiedName)} to have function:${show(name)} but was:${show(functions)}")
        }
        assertThat(functions.first { it.name == name }).all {
            hasParameters(*parameterTypes)
            isVoid()
        }
    }
}

/**
 * Asserts the function has the expected parameter types.
 */
fun Assert<KFunction<*>>.hasParameters(vararg parameterTypes: String) = given { actual ->
    actual.parameters.drop(1).map { it.type.javaType.typeName }.also { parameters ->
        if (parameters != parameterTypes.toList()) {
            expected(
                "function:${show(actual.name)} to have parameters:" +
                    "${show(parameterTypes)} but has:${show(parameters)}"
            )
        }
    }
}

/**
 * Asserts the function has the expected return type.
 */
fun Assert<KFunction<*>>.returns(type: String) = given { actual ->
    actual.returnType.javaType.typeName.also {
        if (it != type) {
            expected("function:${show(actual.name)} to return ${show(type)} but returns:${show(it)}")
        }
    }
}

/**
 * Asserts the function is void.
 */
fun Assert<KFunction<*>>.isVoid() = given { actual ->
    actual.returnType.javaType.typeName.also {
        if (it != "void") {
            expected("function:${show(actual.name)} to be void but returns:${show(it)}")
        }
    }
}
