package org.web3j.examples.obligation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.web3j.corda.model.Party;
import org.web3j.corda.model.SignedTransaction;
import org.web3j.corda.protocol.Corda;
import org.web3j.corda.protocol.CordaService;
import org.web3j.examples.obligation.Obligation.ObligationFlowResource.Issue;
import org.web3j.examples.obligation.Obligation.ObligationFlowResource.Issue.InitiatorParameters;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObligationJavaTest {

    private static Corda corda;
    private static CordaService service;

    @BeforeAll
    public void setUpClass() throws Exception {
        service = new CordaService("http://localhost:9000/");
        corda = Corda.build(service);
    }

    @Test
    public void issueObligation() {
        corda.getNetwork().getNodes().findAll().forEach(System.out::println);

        final Party party = corda.getNetwork().getNodes().findAll().get(2).getLegalIdentities().get(0);

        final InitiatorParameters parameters = new InitiatorParameters(
                "$1", Objects.requireNonNull(party.getName()), false);

        // 1. Normal version, not type-safe
        Object signedTxObject = corda.getCorDapps()
                .findById("obligation-cordapp")
                .getFlows()
                .findById("issue-obligation")
                .start(parameters);

        // Potential runtime exception!
        SignedTransaction signedTx = CordaService.convert(signedTxObject, SignedTransaction.class);
        String name = signedTx.getCoreTransaction().getOutputs().get(0).getData().getLender().getName();
        assertEquals(name, party.getName());

        // 2. web3j generated version, 100% type-safe
        final Issue issue = Obligation.load(corda).getFlows().getIssue();
        signedTx = issue.start(parameters);

        name = signedTx.getCoreTransaction().getOutputs().get(0).getData().getLender().getName();
        assertEquals(name, party.getName());
    }

    @AfterAll
    void tearDownClass() {
        service.close();
    }
}
