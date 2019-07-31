package org.web3j.corda

data class SimpleNodeInfo(
    val addresses: List<NetworkHostAndPort>,
    val legalIdentities: List<Party>
)
