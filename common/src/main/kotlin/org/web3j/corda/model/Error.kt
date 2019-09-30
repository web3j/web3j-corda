package org.web3j.corda.model

data class Error(
    /**
     * The error message.
     * */
    val message: String,
    /**
     * The type of error.
     **/
    val type: String
)
