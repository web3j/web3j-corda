package org.web3j.corda.network

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@DslMarker
@Retention(SOURCE)
@Target(CLASS, FUNCTION)
internal annotation class CordaDslMarker
