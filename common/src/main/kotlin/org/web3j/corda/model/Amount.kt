package org.web3j.corda.model

import javax.annotation.Generated

/**
*
* @param quantity total amount in minor units
* @param displayTokenSize
* @param token
* @param tokenType
*/
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-12-05T15:01:02.723Z"
)
data class Amount(
    /* total amount in minor units */
    val quantity: kotlin.Int,
    val displayTokenSize: java.math.BigDecimal,
    val token: kotlin.String,
    val tokenType: kotlin.String? = null
)
