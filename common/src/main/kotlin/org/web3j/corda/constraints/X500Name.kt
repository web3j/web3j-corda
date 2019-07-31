package org.web3j.corda.constraints

import javax.validation.Constraint
import javax.validation.constraints.Pattern

@Pattern(regexp = "O=, L=, C=")
@Constraint(validatedBy = [])
annotation class X500Name {
}
