package org.web3j.corda.codegen

import java.io.File

interface Generator {

    fun generate(): List<File>
}
