package org.web3j.corda.model

import org.web3j.corda.validation.X500Name

data class Party(
    @field:X500Name
    val name: CordaX500Name,
    val owningKey: PublicKey
)
