/*
 * Copyright 2019 Web3 Labs Ltd.
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

import java.math.BigDecimal;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import org.web3j.corda.examples.obligation.flows.IssueObligation_InitiatorPayload;
import org.web3j.corda.model.AmountCurrency;
import org.web3j.corda.model.core.identity.AbstractParty;
import org.web3j.corda.model.core.identity.Party;
import org.web3j.corda.model.core.transactions.SignedTransaction;
import org.web3j.corda.obligation.api.Obligation;
import org.web3j.corda.protocol.Corda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.web3j.corda.examples.IntegrationTestUtils.getNetwork;

public class ObligationJavaTest {

    @Test
    public void issueObligation() {
        final Corda corda = getNetwork().getParties().get(0).getCorda();

        final Party partyB =
                corda.getApi()
                        .getNetwork()
                        .getNodes()
                        .findByX500Name("O=PartyB,L=New York,C=US")
                        .get(0)
                        .getLegalIdentities()
                        .get(0);

        final AmountCurrency amount = new AmountCurrency(100, BigDecimal.ONE, "GBP");
        final IssueObligation_InitiatorPayload parameters =
                new IssueObligation_InitiatorPayload(amount, partyB, false);

        final Obligation.FlowResource.IssueObligationInitiator issue =
                Obligation.Companion.load(corda.getService())
                        .getFlows()
                        .getIssueObligationInitiator();

        final SignedTransaction signedTx = issue.start(parameters);

        final AbstractParty actualParty =
                Objects.requireNonNull(
                                Objects.requireNonNull(
                                                Objects.requireNonNull(
                                                                signedTx.getCoreTransaction())
                                                        .getOutputs()
                                                        .get(0)
                                                        .getData())
                                        .getParticipants())
                        .get(0);

        assertEquals(partyB.getOwningKey(), actualParty.getOwningKey());
    }
}
