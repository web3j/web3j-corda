package org.web3j.corda.network

import java.util.function.Consumer

/**
 * Corda network DSL entry point extension method.
 */
@CordaDslMarker
fun CordaNetwork.Companion.network(networkBlock: CordaNetwork.() -> Unit): CordaNetwork {
    return networkJava(Consumer { networkBlock.invoke(it) })
}

@CordaDslMarker
fun CordaNetwork.nodes(nodesBlock: CordaNodes.() -> Unit) {
    nodesJava(Consumer { nodesBlock.invoke(it) })
}

@CordaDslMarker
fun CordaNodes.node(nodeBlock: CordaNode.() -> Unit) {
    nodeJava(Consumer { nodeBlock.invoke(it) })
}

@DslMarker
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
internal annotation class CordaDslMarker
