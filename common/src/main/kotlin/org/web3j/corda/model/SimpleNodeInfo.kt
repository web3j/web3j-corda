package org.web3j.corda.model

data class SimpleNodeInfo(
    val addresses: List<NetworkHostAndPort>,
    val legalIdentities: List<Party>
)
