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

import com.github.mustachejava.DefaultMustacheFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.testcontainers.containers.BindMode.READ_WRITE
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.corda.model.LoginRequest
import org.web3j.corda.model.NotaryType
import org.web3j.corda.networkmap.NetworkMapApi
import org.web3j.corda.protocol.Corda
import org.web3j.corda.protocol.CordaService
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.Thread.sleep
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Testcontainers
open class DockerBasedIntegrationTest {

    @TempDir
    lateinit var nodes: File

    companion object {

        val PREFIX = if (System.getProperty("os.name").contains("Mac", true)) "/private" else ""

        const val NETWORK_MAP_ALIAS = "networkmap"

        const val NETWORK_MAP_IMAGE = "cordite/network-map:v0.4.5"
        const val CORDA_ZULU_IMAGE = "corda/corda-zulu-4.1:latest"

        private val network: Network = Network.newNetwork()
        private val timeOut: Duration = Duration.ofMinutes(2)

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

        @JvmStatic
        val NOTARY: KGenericContainer = KGenericContainer(CORDA_ZULU_IMAGE)
                .withNetwork(network)
//                .withExposedPorts(10002, 10003, 10004)
                .withCreateContainerCmdModifier {
                    it.withHostName("notary")
                    it.withName("notary")
                }

        @JvmStatic
        val PARTY_A: KGenericContainer = KGenericContainer(CORDA_ZULU_IMAGE)
                .withEnv("NETWORKMAP_URL", "http://$NETWORK_MAP_ALIAS:8080")
                .withEnv("DOORMAN_URL", "http://$NETWORK_MAP_ALIAS:8080")
                .withEnv("NETWORK_TRUST_PASSWORD", "trustpass")
                .withEnv("MY_PUBLIC_ADDRESS", "http://localhost:10008")
                .withNetwork(network)
                .withCommand("config-generator --generic")
                .withCreateContainerCmdModifier {
                    it.withHostName("partya")
                    it.withName("partya")
                }
                .waitingFor(Wait.forHttp("").forPort(9000).withStartupTimeout(timeOut))
                .withStartupTimeout(timeOut)
                .withExposedPorts(10008, 10009, 10010, 9000)

        @JvmStatic
        val PARTY_B: KGenericContainer = KGenericContainer(CORDA_ZULU_IMAGE)
                .withEnv("NETWORKMAP_URL", "http://$NETWORK_MAP_ALIAS:8080")
                .withEnv("DOORMAN_URL", "http://$NETWORK_MAP_ALIAS:8080")
                .withEnv("NETWORK_TRUST_PASSWORD", "trustpass")
                .withEnv("MY_PUBLIC_ADDRESS", "http://localhost:10011")
                .withNetwork(network)
                .withCommand("config-generator --generic")
                .withCreateContainerCmdModifier {
                    it.withHostName("partyb")
                    it.withName("partyb")
                }
                .waitingFor(Wait.forHttp("").forPort(9000).withStartupTimeout(timeOut))
                .withStartupTimeout(timeOut)
                .withExposedPorts(10011, 10012, 10013, 9000)

        class KGenericContainer(imageName: String) : FixedHostPortGenericContainer<KGenericContainer>(imageName)

        @JvmStatic
        private lateinit var corda: Corda

        @JvmStatic
        private lateinit var service: CordaService
    }

    private fun createNodeConfFiles(name: String, location: String, country: String, p2pPort: Int, rpcPort: Int, adminPort: Int, networkMapUrl: String, file: File, isNotary: Boolean) {
        val nodes = hashMapOf("name" to name,
                "location" to location,
                "country" to country,
                "p2pPort" to p2pPort,
                "rpcPort" to rpcPort,
                "adminPort" to adminPort,
                "networkMapUrl" to networkMapUrl)
        val writer = OutputStreamWriter(FileOutputStream(file))
        val mf = DefaultMustacheFactory()
        val mustache = if (isNotary)
            mf.compile("notaryNodeConf.mustache")
        else
            mf.compile("nodeConf.mustache")
        mustache.execute(PrintWriter(writer), nodes).flush()

    }

    @Test
    fun `test to setup docker containers`() {

        NETWORK_MAP.start()

        val notaryNode = File(nodes, "Notary").apply { mkdir() }
        createNodeConfFiles("Notary", "London", "GB", 10005, 10006, 10007, "http://networkmap:8080", notaryNode.resolve("node.conf"), true)
        getCertificate(notaryNode)

        NOTARY.withEnv("NETWORKMAP_URL", "http://$NETWORK_MAP_ALIAS:8080")
                .withFileSystemBind(PREFIX + notaryNode.absolutePath, "/etc/corda", READ_WRITE)
                .withFileSystemBind(PREFIX + notaryNode.resolve("certificates").absolutePath, "/opt/corda/certificates", READ_WRITE)
                .withEnv("DOORMAN_URL", "http://$NETWORK_MAP_ALIAS:8080")
                .withEnv("NETWORK_TRUST_PASSWORD", "trustpass")
                .withEnv("MY_PUBLIC_ADDRESS", "http://localhost:10005")
                .withCommand("config-generator --generic")
                .start()


        waitForNodeToStart(NOTARY)
        var nodeinfo = NOTARY.execInContainer("find", ".", "-maxdepth", "1", "-name", "nodeInfo*").stdout
        nodeinfo = nodeinfo.substring(2, nodeinfo.length - 1) // remove the ending newline character

        NOTARY.copyFileFromContainer(
                "/opt/corda/$nodeinfo",
                notaryNode.resolve(nodeinfo).absolutePath
        )
        NOTARY.execInContainer("rm", "network-parameters")
        NOTARY.stop()
        sleep(5000)

        updateNotaryInNetworkMap(notaryNode.resolve(nodeinfo).absolutePath)

        sleep(5000)
        NOTARY.start()
        waitForNodeToStart(NOTARY)

        val partyANode = File(nodes, "PartyA").apply { mkdir() }
        createNodeConfFiles("PartyA", "Tokyo", "JP", 10008, 10009, 10010, "http://networkmap:8080", partyANode.resolve("node.conf"), false)
        getCertificate(partyANode)
        PARTY_A.withClasspathResourceMapping("cordapps", "/opt/corda/cordapps", READ_WRITE)
                .withFileSystemBind(PREFIX + partyANode.absolutePath, "/etc/corda", READ_WRITE)
                .withFileSystemBind(PREFIX + partyANode.resolve("certificates").absolutePath, "/opt/corda/certificates", READ_WRITE)
                .start()

        val partyBNode = File(nodes, "PartyB").apply { mkdir() }
        createNodeConfFiles("PartyB", "New York", "US", 10011, 10012, 10013, "http://networkmap:8080", partyBNode.resolve("node.conf"), false)
        getCertificate(partyBNode)
        PARTY_B.withClasspathResourceMapping("cordapps", "/opt/corda/cordapps", READ_WRITE)
                .withFileSystemBind(PREFIX + partyBNode.absolutePath, "/etc/corda", READ_WRITE)
                .withFileSystemBind(PREFIX + partyBNode.resolve("certificates").absolutePath, "/opt/corda/certificates", READ_WRITE)
                .start()
//
//        service = CordaService("http://localhost:${PARTY_A.getMappedPort(9000)}/")
//        corda = Corda.build(service)
//
//        val parties = corda.network.nodes.findAll()
//        parties.apply { forEach(::println) }
//        service.close()
//
        CountDownLatch(1).await()
//        NOTARY.stop()
//        PARTY_A.stop()
//        PARTY_B.stop()
    }

    private fun waitForNodeToStart(node: KGenericContainer) {
        while (!node.logs.contains("started up and registered")) {
            sleep(5000)
            println("waiting for ${node.containerName} to start")
        }
    }

    private fun getCertificate(node: File) {
        val workingDir = File(System.getProperty("user.dir"))

        val certificateFolder = File(node, "certificates").apply { mkdir() }
        val certificateFile = certificateFolder.resolve("network-root-truststore.jks")
        val networkTrust =
                "curl http://localhost:8080/network-map/truststore -o $certificateFile"
        runCommand(workingDir, networkTrust)

//         NetworkMapApi.build(CordaService("http://localhost:8080")).apply {
//             Files.write(certificateFile.toPath(), IOUtils.toByteArray(networkMap.truststore))
//         }
    }

    private fun updateNotaryInNetworkMap(nodeInfoPath: String) {
        var networkMapApi = NetworkMapApi.build(CordaService("http://localhost:8080"))
        val loginRequest = LoginRequest("sa", "admin")

        val token = networkMapApi.admin.login(loginRequest)
        networkMapApi = NetworkMapApi.build(CordaService("http://localhost:8080"), token)
        networkMapApi.admin.notaries.create(NotaryType.NON_VALIDATING, Files.readAllBytes(Paths.get(nodeInfoPath)))
    }

    private fun runCommand(workingDir: File, command: String) {
        ProcessBuilder(command.split(" "))
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor(60, TimeUnit.MINUTES)
    }
}
