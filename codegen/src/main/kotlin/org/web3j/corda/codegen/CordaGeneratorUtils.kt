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