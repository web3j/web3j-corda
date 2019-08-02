package org.web3j.corda.api

import org.web3j.corda.model.CordaX500Name
import org.web3j.corda.model.SimpleNodeInfo
import org.web3j.corda.validation.HostAndPort
import org.web3j.corda.validation.X500Name
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

interface NodeResource {

    @get:GET
    @get:Path("self")
    val self: SimpleNodeInfo

    /**
     * Retrieves all nodes.
     */
    @GET
    fun findAll(): List<SimpleNodeInfo>

    /**
     * Retrieves by the supplied host and port.
     *
     * @param hostAndPort `host:port` for the Corda P2P of the node
     */
    @GET
    fun findByHostAndPort(
        @Valid
        @HostAndPort
        @QueryParam("hostAndPort")
        hostAndPort: String
    ): List<SimpleNodeInfo>

    /**
     * Retrieves by the supplied X500 name.
     *
     * @param x500Name `host:port` for the Corda P2P of the node
     */
    @GET
    fun findByX500Name(
        @Valid
        @X500Name
        @QueryParam("x500Name")
        x500Name: CordaX500Name
    ): List<SimpleNodeInfo>
}
