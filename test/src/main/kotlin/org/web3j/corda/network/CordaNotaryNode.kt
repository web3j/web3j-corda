/*
 * Copyright 2019 Web3 Labs Ltd.
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
package org.web3j.corda.network

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import org.web3j.corda.networkmap.LoginRequest
import org.web3j.corda.networkmap.NotaryType
import org.web3j.corda.protocol.CordaService
import org.web3j.corda.protocol.NetworkMap
import org.web3j.corda.testcontainers.KGenericContainer

/**
 * Corda network notary node.
 */
class CordaNotaryNode internal constructor(network: CordaNetwork) : CordaNode(network) {

    /**
     * Is this a validating notary?
     */
    var validating: Boolean = false

    override fun KGenericContainer.configure(nodeDir: File) {
        logger.info("Starting notary container $canonicalName...")
        start()
        logger.info("Started notary container $canonicalName.")
        val prev = this@CordaNotaryNode.network.api.admin.networkMap().networkParameterHash
        extractNotaryNodeInfo(nodeDir).also {
            updateNotaryInNetworkMap(nodeDir.resolve(it).absolutePath)
        }
        waitForNetworkMapHashUpdate(prev)
        logger.info("Stopping notary container $canonicalName...")
        stop()
        logger.info("Stopped notary container $canonicalName.")
    }

    private fun KGenericContainer.extractNotaryNodeInfo(notaryNodeDir: File): String {
        logger.info("Extracting notary info from container $canonicalName...")
        return run {
            execInContainer("find", ".", "-maxdepth", "1", "-name", "nodeInfo*").stdout.run {
                substring(2, length - 1) // remove relative path and the ending newline character
            }.also {
                val nodeInfoPath = notaryNodeDir.resolve(it).absolutePath
                logger.info("Copying notary folder from /opt/corda/$it to $nodeInfoPath")
                copyFileFromContainer("/opt/corda/$it", nodeInfoPath)
                logger.info("Removing folder from /opt/corda/$it")
                execInContainer("rm", "network-parameters")
                logger.info("Extracted notary info from container $canonicalName.")
            }
        }
    }

    private fun updateNotaryInNetworkMap(nodeInfoPath: String) {
        val loginRequest = LoginRequest("sa", "admin")
        val token = network.api.admin.login(loginRequest)
        val authMap = NetworkMap.build(CordaService(network.service.uri), token)

        val notaryType = if (validating) NotaryType.VALIDATING else NotaryType.NON_VALIDATING
        logger.info("Creating a $notaryType notary in network map with node info $nodeInfoPath")
        authMap.api.admin.notaries.create(notaryType, Files.readAllBytes(Paths.get(nodeInfoPath)))
    }

    private fun waitForNetworkMapHashUpdate(prevNetworkMapHash: String) {
        while (network.api.admin.networkMap().networkParameterHash == prevNetworkMapHash) {
            TimeUnit.MILLISECONDS.sleep(5000)
            logger.info("waiting for Network Map to update the Hash ")
        }
    }
}
