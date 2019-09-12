package org.web3j.corda.codegen

import java.io.File

interface CordaGenerator {

    fun generate(): List<File>
}
