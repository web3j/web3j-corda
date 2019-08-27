/*
 * Copyright 2019 Web3 Labs LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.corda.codegen.generated.obligation.api

import org.web3j.corda.api.CorDapp
import org.web3j.corda.api.Flow
import org.web3j.corda.api.FlowResource
import org.web3j.corda.codegen.generated.obligation.model.SettleObligationInitiatorParameters
import org.web3j.corda.dapps.LifeCycle
import org.web3j.corda.model.SignedTransaction
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.ProxyBuilder
import java.io.File
import javax.annotation.Generated
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces

/**
*  CorDapp wrapper.
*/
@Path("/api/rest/cordapps/test-cordapp/")
@Generated(
    value = ["org.web3j.corda.codegen.CorDappGenerator"],
    date = ""
)
interface Test : CorDapp {
    @get:Path("flows")
    override val flows: TestFlowResource

    interface TestFlowResource : FlowResource {
        /**
         * Get the Settle flow.
         */
        @get:Path("settle-test")
        val settle: Settle

        /**
         * Test Settle flow.
         */
        interface Settle : Flow {

            /**
             * Start the Settle flow.
             */
            @POST
            @Produces("application/json")
            @Consumes("application/json")
            fun start(@Valid parameters: SettleObligationInitiatorParameters): SignedTransaction
        }
    }

    /**
     * Test CorDapp lifecycle methods.
     */
    companion object : LifeCycle<Test> {

        override fun upgrade(corda: Corda, file: File): Test =
            TODO("not implemented")

        override fun deploy(corda: Corda, file: File): Test =
            TODO("not implemented")

        /**
         * Loads an existing Test CorDapp instance.
         */
        @JvmStatic
        override fun load(corda: Corda) =
            ProxyBuilder.build(Test::class.java, corda.service)
    }
}
