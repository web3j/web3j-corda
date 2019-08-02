package org.web3j.corda.api

import org.web3j.corda.model.Party
import org.web3j.corda.model.SimpleNodeInfo
import javax.ws.rs.GET
import javax.ws.rs.Path

interface NetworkResource {

    @get:Path("nodes")
    val nodes: NodeResource

    @get:GET
    @get:Path("my-node-info")
    val myNodeInfo: SimpleNodeInfo

    @get:GET
    @get:Path("notaries")
    val notaries: List<Party>
}
