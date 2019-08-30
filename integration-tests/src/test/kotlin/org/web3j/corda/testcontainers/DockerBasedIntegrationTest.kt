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
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.corda.model.LoginRequest
import org.web3j.corda.model.NotaryType
import org.web3j.corda.networkmap.NetworkMapApi
import org.web3j.corda.protocol.CordaService
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
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
        const val NETWORK_MAP_URL = "http://${NETWORK_MAP_ALIAS}:8080"

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

        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
    }

    @Test
    fun `test to setup docker containers`() {

        NETWORK_MAP.start()

        val notaryName = "Notary"
        val notaryNodeDir = nodes.resolve(notaryName)

        val notary = createNodeContainer(notaryName, "London", "GB", 10005, 10006, 10007, true)
        notary.start()

        val nodeInfo = extractNotaryNodeInfo(notary, notaryNodeDir)
        notary.stop()

        updateNotaryInNetworkMap(notaryNodeDir.resolve(nodeInfo).absolutePath)

        notary.start()

        val partyA = createNodeContainer("PartyA", "Tokyo", "JP", 10008, 10009, 10010, false)
        partyA.start()

        val partyB = createNodeContainer("PartyB", "New York", "US", 10011, 10012, 10013, false)
        partyB.start()

        CountDownLatch(1).await()
        notary.stop()
        partyA.stop()
        partyB.stop()
        NETWORK_MAP.stop()
    }

    private fun createNodeContainer(name: String, location: String, country: String, p2pPort: Int, rpcPort: Int, adminPort: Int, isNotary: Boolean): KGenericContainer {
        val nodeDir = File(nodes, name).apply { mkdir() }
        createNodeConfFiles(name, location, country, p2pPort, rpcPort, adminPort, nodeDir.resolve("node.conf"), isNotary)
        getCertificate(nodeDir)
        val node = KGenericContainer(CORDA_ZULU_IMAGE)
                .withNetwork(network)
                .withExposedPorts(p2pPort, rpcPort, adminPort)
                .withFileSystemBind(PREFIX + nodeDir.absolutePath, "/etc/corda", READ_WRITE)
                .withFileSystemBind(PREFIX + nodeDir.resolve("certificates").absolutePath, "/opt/corda/certificates", READ_WRITE)
                .withEnv("NETWORKMAP_URL", NETWORK_MAP_URL)
                .withEnv("DOORMAN_URL", NETWORK_MAP_URL)
                .withEnv("NETWORK_TRUST_PASSWORD", "trustpass")
                .withEnv("MY_PUBLIC_ADDRESS", "http://localhost:$p2pPort")
                .withCommand("config-generator --generic")
                .withStartupTimeout(timeOut)
                .withCreateContainerCmdModifier {
                    it.withHostName(name.toLowerCase())
                    it.withName(name.toLowerCase())
                }

        if (!isNotary) {
            node.withClasspathResourceMapping("cordapps", "/opt/corda/cordapps", READ_WRITE)
        }
        return node
    }

    private fun createNodeConfFiles(name: String, location: String, country: String, p2pPort: Int, rpcPort: Int, adminPort: Int, file: File, isNotary: Boolean) {
        val nodes = hashMapOf("name" to name,
                "location" to location,
                "country" to country,
                "p2pPort" to p2pPort,
                "rpcPort" to rpcPort,
                "adminPort" to adminPort,
                "networkMapUrl" to NETWORK_MAP_URL)
        val writer = OutputStreamWriter(FileOutputStream(file))
        val mf = DefaultMustacheFactory()
        val mustache = if (isNotary)
            mf.compile("notaryNodeConf.mustache")
        else
            mf.compile("nodeConf.mustache")
        mustache.execute(PrintWriter(writer), nodes).flush()

    }

    private fun getCertificate(node: File) {
        val workingDir = File(System.getProperty("user.dir"))

        val certificateFolder = File(node, "certificates").apply { mkdir() }
        val certificateFile = certificateFolder.resolve("network-root-truststore.jks")
        val networkTrust =
                "curl http://localhost:${NETWORK_MAP.getMappedPort(8080)}/network-map/truststore -o $certificateFile"
        runCommand(workingDir, networkTrust)

//         NetworkMapApi.build(CordaService("http://localhost:8080")).apply {
//             Files.write(certificateFile.toPath(), IOUtils.toByteArray(networkMap.truststore))
//         }
    }

    private fun extractNotaryNodeInfo(notary: KGenericContainer, notaryNode: File): String {
        var nodeinfo = notary.execInContainer("find", ".", "-maxdepth", "1", "-name", "nodeInfo*").stdout
        nodeinfo = nodeinfo.substring(2, nodeinfo.length - 1) // remove the ending newline character

        notary.copyFileFromContainer(
                "/opt/corda/$nodeinfo",
                notaryNode.resolve(nodeinfo).absolutePath
        )
        notary.execInContainer("rm", "network-parameters")
        return nodeinfo
    }

    private fun updateNotaryInNetworkMap(nodeInfoPath: String) {
        var networkMapApi = NetworkMapApi.build(CordaService("http://localhost:${NETWORK_MAP.getMappedPort(8080)}"))
        val loginRequest = LoginRequest("sa", "admin")

        val token = networkMapApi.admin.login(loginRequest)
        networkMapApi = NetworkMapApi.build(CordaService("http://localhost:${NETWORK_MAP.getMappedPort(8080)}"), token)
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
