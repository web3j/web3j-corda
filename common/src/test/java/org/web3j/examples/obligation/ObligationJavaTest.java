package org.web3j.examples.obligation;

import org.junit.Test;
import org.web3j.corda.Party;
import org.web3j.corda.SignedTransaction;
import org.web3j.corda.protocol.Corda;
import org.web3j.corda.protocol.CordaService;

public class ObligationJavaTest {

    @Test
    public void issueObligation() {
        Party party = new Party("", "");
        CordaService service = new CordaService("http://localhost:9000/");

        Corda corda = Corda.build(service);

        corda.getNetwork().getAllNodes().forEach(System.out::println);

        SignedTransaction signedTx = (SignedTransaction) corda
                .getCorDappById("obligation-cordapp")
                .getFlowById("issue-obligation")
                .start(party);

        Obligation.Issue issue = Obligation.Issue.build(corda);
        issue.start(party);
    }
}
