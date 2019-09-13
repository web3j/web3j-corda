package org.web3j.corda.network

class CordaNodes internal constructor(private val network: CordaNetwork) : Iterable<CordaNode> {

    private val nodes = mutableListOf<CordaNode>()

    override fun iterator() = nodes.iterator()

    fun node(nodeBlock: CordaNode.() -> Unit) {
        CordaNode(network).also {
            nodeBlock.invoke(it)
            it.validate()
            nodes.add(it)
            if (it.autoStart) {
                it.start()
            }
        }
    }
}
