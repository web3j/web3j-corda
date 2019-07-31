package org.web3j.corda.constraints

import javax.validation.Constraint
import javax.validation.constraints.Pattern

@Pattern(regexp = ":")
@Constraint(validatedBy = [])
annotation class HostAndPort {
}
