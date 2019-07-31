package org.web3j.corda

data class SignedTransaction(
    val txBits: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SignedTransaction

        if (!txBits.contentEquals(other.txBits)) return false

        return true
    }

    override fun hashCode(): Int {
        return txBits.contentHashCode()
    }
}
