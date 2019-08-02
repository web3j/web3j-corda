package org.web3j.corda.api

import javax.ws.rs.Path

interface CorDapp {

    @get:Path("flows")
    val flows: FlowResource
}
