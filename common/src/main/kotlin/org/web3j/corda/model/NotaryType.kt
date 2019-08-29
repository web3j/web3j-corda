package org.web3j.corda.model

enum class NotaryType(val value: String) {

    VALIDATING("validating"),
    NON_VALIDATING("nonValidating");

    override fun toString(): String {
        return value
    }
}
