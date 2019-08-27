/*
 * Copyright 2019 Web3 Labs LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.corda.testcontainers

import org.junit.Test
import org.testcontainers.containers.BindMode.READ_WRITE
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import java.io.File
import java.lang.Thread.sleep
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Testcontainers
open class DockerBasedIntegrationTest {

    companion object {
        const val NETWORK_MAP_ALIAS = "networkmap"

        const val NETWORK_MAP_IMAGE = "cordite/network-map:v0.4.5"
        const val CORDA_ZULU_IMAGE = "corda/corda-zulu-4.1:latest"

        private val network: Network = Network.newNetwork()
        private val timeOut: Duration = Duration.ofMinutes(2)

        @Container
        @JvmStatic
        val NETWORK_MAP: KGenericContainer = KGenericContainer(NETWORK_MAP_IMAGE)
            .withCreateContainerCmdModifier {
                it.withHostName(NETWORK_MAP_ALIAS)
                it.withName(NETWORK_MAP_ALIAS)
            }
            .withNetwork(network)
            .withNetworkAliases(NETWORK_MAP_ALIAS)
            .withEnv(mapOf(Pair("NMS_STORAGE_TYPE", "file")))
            .waitingFor(Wait.forHttp("").forPort(8080))
            .withFixedExposedPort(8080, 8080)

        @Container
        @JvmStatic
        val NOTARY: KGenericContainer = KGenericContainer(CORDA_ZULU_IMAGE)
            .withNetwork(network)
//                .withExposedPorts(10002, 10003, 10004)
            .withCreateContainerCmdModifier {
                it.withHostName("notary")
                it.withName("notary")
            }

        @Container
        @JvmStatic
        val PARTY_A: KGenericContainer = KGenericContainer(CORDA_ZULU_IMAGE)
            .withEnv("NETWORKMAP_URL", "http://$NETWORK_MAP_ALIAS:8080")
            .withEnv("DOORMAN_URL", "http://$NETWORK_MAP_ALIAS:8080")
            .withEnv("NETWORK_TRUST_PASSWORD", "trustpass")
            .withEnv("MY_PUBLIC_ADDRESS", "http://localhost:10005")
            .withNetwork(network)
            .withCommand("config-generator --generic")
            .withCreateContainerCmdModifier {
                it.withHostName("partya")
                it.withName("partya")
            }
            .waitingFor(Wait.forHttp("").forPort(9000).withStartupTimeout(timeOut))
            .withStartupTimeout(timeOut)
            .withExposedPorts(10005, 10006, 10007, 10046, 9000)

        @Container
        @JvmStatic
        val PARTY_B: KGenericContainer = KGenericContainer(CORDA_ZULU_IMAGE)
            .withEnv("NETWORKMAP_URL", "http://$NETWORK_MAP_ALIAS:8080")
            .withEnv("DOORMAN_URL", "http://$NETWORK_MAP_ALIAS:8080")
            .withEnv("NETWORK_TRUST_PASSWORD", "trustpass")
            .withEnv("MY_PUBLIC_ADDRESS", "http://localhost:10008")
            .withNetwork(network)
            .withCommand("config-generator --generic")
            .withCreateContainerCmdModifier {
                it.withHostName("partyb")
                it.withName("partyb")
            }
            .waitingFor(
                Wait.forHttp("")
                    .forPort(9000)
                    .withStartupTimeout(timeOut)
            )
            .withStartupTimeout(timeOut)
            .withExposedPorts(10008, 10009, 10010, 10049, 9000)

        class KGenericContainer(imageName: String) : FixedHostPortGenericContainer<KGenericContainer>(imageName)

        @JvmStatic
        private lateinit var corda: Corda

        @JvmStatic
        private lateinit var service: CordaService
    }

    @Test
    fun `test to setup docker containers`() {
        val workingDir = File(System.getProperty("user.dir"))
        val updateNotary = File(javaClass.classLoader.getResource("updateNotary.sh")?.toURI()!!)
        val createNodeConfs = File(javaClass.classLoader.getResource("createNodeConfs.sh")?.toURI()!!)

        // create Notary credentials
        runCommand(workingDir, "mkdir -p out/test/resources/nodes/Notary")
        runCommand(workingDir, "mkdir -p out/test/resources/nodes/Notary/certificates")
        runCommand(workingDir, createNodeConfs, "Notary", "O=Notary,L=London,C=GB", "10002", NETWORK_MAP_ALIAS)

        // create PartyA credentials
        runCommand(workingDir, "mkdir -p out/test/resources/nodes/PartyA")
        runCommand(workingDir, "mkdir -p out/test/resources/nodes/PartyA/certificates")
        runCommand(workingDir, createNodeConfs, "PartyA", "O=PartyA,L=London,C=GB", "10005", NETWORK_MAP_ALIAS)

        // create PartyB credentials
        runCommand(workingDir, "mkdir -p out/test/resources/nodes/PartyB")
        runCommand(workingDir, "mkdir -p out/test/resources/nodes/PartyB/certificates")
        runCommand(workingDir, createNodeConfs, "PartyB", "O=PartyB,L=New York,C=US", "10008", NETWORK_MAP_ALIAS)

        NETWORK_MAP.start()


        getCertificate(workingDir, "Notary")
        NOTARY.withEnv("NETWORKMAP_URL", "http://$NETWORK_MAP_ALIAS:8080")
            .withClasspathResourceMapping("nodes/Notary", "/etc/corda", READ_WRITE)
            .withClasspathResourceMapping("nodes/Notary/certificates", "/opt/corda/certificates", READ_WRITE)
            .withEnv("DOORMAN_URL", "http://$NETWORK_MAP_ALIAS:8080")
            .withEnv("NETWORK_TRUST_PASSWORD", "trustpass")
            .withEnv("MY_PUBLIC_ADDRESS", "http://localhost:10002")
            .withCommand("config-generator --generic")
            .start()

        waitForNodeToStart(NOTARY)
        var nodeinfo = NOTARY.execInContainer("find", ".", "-maxdepth", "1", "-name", "nodeInfo*").stdout
        nodeinfo = nodeinfo.substring(2, nodeinfo.length - 1) // remove the ending newline character

        NOTARY.copyFileFromContainer(
            "/opt/corda/$nodeinfo",
            "${workingDir.absolutePath}/out/test/resources/nodes/Notary/$nodeinfo"
        )
        NOTARY.execInContainer("rm", "network-parameters")
        NOTARY.stop()
        sleep(5000)

        runCommand(workingDir, updateNotary, NETWORK_MAP_ALIAS)
        sleep(5000)
        NOTARY.start()
        waitForNodeToStart(NOTARY)

        getCertificate(workingDir, "PartyA")
        PARTY_A.withClasspathResourceMapping("nodes/PartyA/cordapps", "/opt/corda/cordapps", READ_WRITE)
            .withClasspathResourceMapping("nodes/PartyA", "/etc/corda", READ_WRITE)
            .withClasspathResourceMapping("nodes/PartyA/certificates", "/opt/corda/certificates", READ_WRITE)
            .start()

        getCertificate(workingDir, "PartyB")
        PARTY_B.withClasspathResourceMapping("nodes/PartyB/cordapps", "/opt/corda/cordapps", READ_WRITE)
            .withClasspathResourceMapping("nodes/PartyB", "/etc/corda", READ_WRITE)
            .withClasspathResourceMapping("nodes/PartyB/certificates", "/opt/corda/certificates", READ_WRITE)
            .start()

        service = CordaService("http://localhost:${PARTY_A.getMappedPort(9000)}/")
        corda = Corda.build(service)

        val parties = corda.network.nodes.findAll()
        parties.apply { forEach(::println) }
        service.close()

        CountDownLatch(1).await()
        NOTARY.stop()
        PARTY_A.stop()
        PARTY_B.stop()
    }

    private fun waitForNodeToStart(node: KGenericContainer) {
        while (!node.logs.contains("started up and registered")) {
            sleep(5000)
            println("waiting for ${node.containerName} to start")
        }
    }

    private fun getCertificate(workingDir: File, node: String) {

        val nodeCertificatePath = "${javaClass.classLoader.getResource("nodes/$node")!!.path}/certificates"

        runCommand(workingDir, "rm -rf $nodeCertificatePath")
        runCommand(workingDir, "mkdir -p $nodeCertificatePath")
        val networkTrust =
            "curl http://localhost:8080/network-map/truststore -o $nodeCertificatePath/network-root-truststore.jks"
        runCommand(workingDir, networkTrust)
    }

    private fun runCommand(workingDir: File, command: String) {
        ProcessBuilder(command.split(" "))
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor(60, TimeUnit.MINUTES)
    }

    private fun runCommand(workingDir: File, script: File, vararg arguments: String) {
        ProcessBuilder(listOf<String>(script.absolutePath) + arguments)
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor(60, TimeUnit.MINUTES)
    }
}
