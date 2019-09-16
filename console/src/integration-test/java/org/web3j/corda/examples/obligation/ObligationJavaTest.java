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
package org.web3j.corda.examples.obligation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.web3j.corda.codegen.generated.obligation.api.Obligation;
import org.web3j.corda.codegen.generated.obligation.model.IssueObligationInitiatorParameters;
import org.web3j.corda.model.Party;
import org.web3j.corda.model.SignedTransaction;
import org.web3j.corda.protocol.Corda;
import org.web3j.corda.protocol.CordaService;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.web3j.corda.util.CordaUtils.convert;

public class ObligationJavaTest {

    private static Corda corda;
    private static CordaService service;

    @BeforeAll
    static void setUpClass() throws Exception {
        service = new CordaService("http://localhost:9000/", 5000, 5000);
        corda = Corda.build(service);
    }

    @Test
    public void issueObligation() {
        corda.getNetwork().getNodes().findAll().forEach(System.out::println);

        final Party party =
                corda.getNetwork().getNodes().findAll().get(2).getLegalIdentities().get(0);

        final IssueObligationInitiatorParameters parameters =
                new IssueObligationInitiatorParameters(
                        "$1", Objects.requireNonNull(party.getName()), false);

        // 1. Normal version, not type-safe
        Object signedTxObject =
                corda.getCorDapps()
                        .findById("obligation-cordapp")
                        .getFlows()
                        .findById("issue-obligation")
                        .start(parameters);

        // Potential runtime exception!
        SignedTransaction signedTx = convert(signedTxObject, SignedTransaction.class);
        String name =
                signedTx.getCoreTransaction()
                        .getOutputs()
                        .get(0)
                        .getData()
                        .getParticipants()
                        .get(0)
                        .getOwningKey();
        assertEquals(name, party.getOwningKey());

        // 2. web3j generated version, 100% type-safe
        final Obligation.ObligationFlowResource.Issue issue =
                Obligation.load(corda).getFlows().getIssue();
        signedTx = issue.start(parameters);

        name =
                signedTx.getCoreTransaction()
                        .getOutputs()
                        .get(0)
                        .getData()
                        .getParticipants()
                        .get(0)
                        .getOwningKey();
        assertEquals(name, party.getOwningKey());
    }

    @AfterAll
    static void tearDownClass() {
        service.close();
    }
}
