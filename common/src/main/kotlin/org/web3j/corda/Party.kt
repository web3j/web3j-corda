package org.web3j.corda

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Party @JsonCreator constructor(
    @field:JsonProperty("owningKey") val owningKey: PublicKey,
    @field:JsonProperty("name") val name: CordaX500Name
)
