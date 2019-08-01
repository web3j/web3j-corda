package org.web3j.corda.dapps

import org.web3j.corda.protocol.Corda
import java.io.File

interface LifeCycle<T> {
    fun load(corda: Corda): T
    fun deploy(corda: Corda, file: File): T
    fun upgrade(corda: Corda, file: File): T
}
