package org.web3j.corda.api

import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * General CorDapp flow logic resources, eg. progress tracker etc.
 */
interface Flow {

    @get:Path("progress-tracker")
    val progressTracker: ProgressTracker
}

interface StartableFlow : Flow {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun start(@Valid parameters: Any): Any
}
