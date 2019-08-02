package org.web3j.corda.api

import javax.ws.rs.GET
import javax.ws.rs.Path

interface StepsResource {

    @GET
    fun findAll(): List<ProgressTracker.Step>

    @get:GET
    @get:Path("current")
    val current: ProgressTracker.Step

    @get:GET
    @get:Path("next")
    val next: ProgressTracker.Step
}
