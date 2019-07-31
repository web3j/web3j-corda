package org.web3j.examples.obligation

import org.web3j.corda.Party
import org.web3j.corda.SignedTransaction
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService

fun main() {
    val party = Party("", "")
    val service = CordaService("http://localhost:9000/")

    val corda = Corda.build(service)
    corda.network.nodes.forEach { println(it) }

    // 1. Normal version, not type safe
    val signedTx = corda
        .corDappById("obligation-cordapp")
        .flowById("issue-obligation")
        .start(party) as SignedTransaction

    // 2. web3j generated version, 100% type safe
    Obligation.Issue.Builder().build(corda).start(party)
}
