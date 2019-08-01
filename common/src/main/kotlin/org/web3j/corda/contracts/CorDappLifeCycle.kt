package org.web3j.corda.contracts

import org.web3j.corda.protocol.Corda
import java.io.File

interface CorDappLifeCycle<T> {
    fun load(corda: Corda): T
    fun deploy(corda: Corda, file: File): T
    fun upgrade(corda: Corda, file: File): T
}
