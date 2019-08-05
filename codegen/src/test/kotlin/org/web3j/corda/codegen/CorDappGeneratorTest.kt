package org.web3j.corda.codegen

import org.junit.jupiter.api.Test

class CorDappGeneratorTest {
    @Test
    internal fun `generate obligation CorDapp`() {
        CorDappGenerator.obligationGenerator()
    }
}