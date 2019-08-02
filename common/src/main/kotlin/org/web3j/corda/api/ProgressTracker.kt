package org.web3j.corda.api

import javax.ws.rs.Path

interface ProgressTracker {

    @get:Path("steps")
    val steps: StepsResource

    interface Step {
        val label: String
    }
}
