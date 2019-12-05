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
package org.web3j.corda.codegen

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.ruleset.experimental.ExperimentalRuleSetProvider
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import com.samskivert.mustache.Mustache
import mu.KLogging
import org.openapitools.codegen.templating.mustache.CamelCaseLambda
import org.openapitools.codegen.templating.mustache.LowercaseLambda
import org.openapitools.codegen.templating.mustache.TitlecaseLambda
import org.openapitools.codegen.templating.mustache.UppercaseLambda
import java.io.File

internal object CordaGeneratorUtils : KLogging() {

    private val ruleSets = listOf(
        StandardRuleSetProvider().get(),
        ExperimentalRuleSetProvider().get()
    )

    /**
     * Repackage a given class name onto a conflict-safe name.
     */
    fun repackage(name: String, mapping: Map<String, String>): String {
        return mapping.keys.firstOrNull {
            name.startsWith(it)
        }?.let {
            name.replace(it, mapping[it] ?: error("key '$it' not found"))
        } ?: appendPrefix("generated", name)
    }

    /**
     * Append the given prefix to a name if it doesn't contain it already or is a Kotlin class.
     */
    fun appendPrefix(prefix: String, name: String): String {
        return if (!name.startsWith(prefix) &&
            !name.startsWith("kotlin") &&
            !name.startsWith("java") &&
            !name.startsWith("org.web3j.corda.model")) {
            "$prefix.$name"
        } else {
            name
        }
    }

    fun addLambdas(context: MutableMap<String, Any>) {
        context["lowercase"] = LowercaseLambda()
        context["uppercase"] = UppercaseLambda()
        context["camelcase"] = CamelCaseLambda()
        context["titlecase"] = TitlecaseLambda()
        context["unquote"] = Mustache.Lambda { fragment, out ->
            out.write(fragment.execute().removeSurrounding("\""))
        }
    }

    /**
     * Format a given Kotlin file using KtLint.
     */
    fun kotlinFormat(file: File) {
        KtLint.format(
            KtLint.Params(
                ruleSets = ruleSets,
                cb = { error, _ ->
                    logger.warn { error }
                },
                text = file.readText(),
                debug = true
            )
        ).run {
            file.writeText(this)
        }
    }
}
