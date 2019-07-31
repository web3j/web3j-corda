package org.web3j.corda.validation

import javax.validation.Constraint
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import kotlin.reflect.KClass

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.VALUE_PARAMETER
)
@MustBeDocumented
@Constraint(validatedBy = [])
@Pattern(regexp = HostAndPort.REGEXP)
@Retention(AnnotationRetention.RUNTIME)
@ReportAsSingleViolation
annotation class HostAndPort(
    val message: String = "{org.web3j.corda.validation.HostAndPort}",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Any>> = []
) {
    companion object {
        const val REGEXP = ""
    }
}
