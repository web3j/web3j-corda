package org.web3j.corda

import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class CordaIntegrationTest {

    companion object {

        @Container
        @JvmStatic
        private val CORDA = KGenericContainer("corda/corda-zulu-4.1:latest")
//            .withClasspathResourceMapping("aion/config", "/aion/custom/config", READ_WRITE)
//            .withClasspathResourceMapping("aion/log", "/aion/custom/log", READ_WRITE)
//            .withCommand("/aion/aion.sh --network custom")
//            .withExposedPorts(8545)

        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
    }
}
