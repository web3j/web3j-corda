package org.web3j.corda.api

import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("api/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface CordaApi {

    @get:Path("cordapps")
    val corDapps: CorDappResource

    @get:Path("network")
    val network: NetworkResource
}
