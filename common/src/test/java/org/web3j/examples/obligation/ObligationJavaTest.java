package org.web3j.examples.obligation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.web3j.corda.model.Party;
import org.web3j.corda.model.SignedTransaction;
import org.web3j.corda.protocol.Corda;
import org.web3j.corda.protocol.CordaService;
import org.web3j.examples.obligation.ObligationCorDapp.Obligation.InitiatorParameters;

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
        corda.getNetwork().getAllNodes().forEach(System.out::println);

        final Party party = corda.getNetwork()
                .getAllNodes().get(2)
                .getLegalIdentities().get(0);

        final InitiatorParameters parameters =
                new InitiatorParameters("$1", party.getName(), false);

        SignedTransaction signedTx = (SignedTransaction) corda
                .getCorDappById("obligation-cordapp")
                .getFlowById("issue-obligation")
                .start(parameters);

        final ObligationCorDapp.Obligation.Issue issue =
                ObligationCorDapp.load(corda).getObligation().getIssue();

        issue.start(parameters);
    }

    @AfterAll
    void tearDownClass() {
        service.close();
    }
}
