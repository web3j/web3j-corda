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

import java.io.File;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import org.web3j.corda.examples.obligation.flows.IssueObligation_InitiatorPayload;
import org.web3j.corda.model.AmountCurrency;
import org.web3j.corda.model.core.identity.AbstractParty;
import org.web3j.corda.model.core.identity.Party;
import org.web3j.corda.model.core.transactions.SignedTransaction;
import org.web3j.corda.network.CordaNetwork;
import org.web3j.corda.obligation.api.Obligation;
import org.web3j.corda.protocol.Corda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.web3j.corda.network.CordaNetwork.network;

public class ObligationJavaTest {

    private static CordaNetwork network =
            network(
                    net -> {
                        net.setBaseDir(
                                new File(
                                        ObligationJavaTest.class
                                                .getClassLoader()
                                                .getResource("classes")
                                                .getFile()));
                        net.nodes(
                                nodes -> {
                                    nodes.node(
                                            node -> {
                                                node.setName("O=Notary,L=London,C=GB");
                                                node.setNotary(true);
                                            });
                                    nodes.node(node -> node.setName("O=PartyA,L=London,C=GB"));
                                    nodes.node(node -> node.setName("O=PartyB,L=New York,C=US"));
                                });
                    });

    @Test
    public void issueObligation() {
        final Corda corda = network.getNodes().get("O=PartyA,L=London,C=GB").getApi();

        final Party party =
                corda.getNetwork().getNodes().findAll().get(2).getLegalIdentities().get(0);

        final AmountCurrency amount = new AmountCurrency(100, BigDecimal.ONE, "GBP");
        final IssueObligation_InitiatorPayload parameters =
                new IssueObligation_InitiatorPayload(amount, party, false);

        final Obligation.FlowResource.IssueObligationInitiator issue =
                Obligation.Companion.load(corda.getService())
                        .getFlows()
                        .getIssueObligationInitiator();

        final SignedTransaction signedTx = issue.start(parameters);

        final AbstractParty actualParty =
                signedTx.getCoreTransaction()
                        .getOutputs()
                        .get(0)
                        .getData()
                        .getParticipants()
                        .get(0);

        assertEquals(party.getOwningKey(), actualParty.getOwningKey());
    }
}
