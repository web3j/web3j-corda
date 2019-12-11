Test network reference
======================

Web3j Corda provides a DSL that allows you to automatically generate and configure a set of nodes for testing
(in a way, is quite similar to a [`CordForm`](https://docs.corda.net/generating-a-node.html#the-cordform-task) Gradle task). 
Here is an example of a Corda network that creates three nodes and start them on Docker containers:

```kotlin
val myNetwork = network {
    directory = File("<path-to-corDapp>")
    nodes {
        notary {
            name = "O=Notary, L=London, C=GB"
            validating = true
        }
        party {
            name = "O=PartyA, L=London, C=GB"
        }
        party {
            name = "O=PartyB, L=New York, C=US"
        }
    }
}
```

Running this task will create six Docker containers:

* A `Notary` node that offers a validating notary service.
* `PartyA` and `PartyB` nodes that:
    * Are not offering any services.
    * Have an RPC user, `user1` by default, that will be used by Braid to log into the node via RPC.
* For each party node, a [Braid](https://gitlab.com/bluebank/braid) server exposing their CorDapps using Open API.
* A [Network Map](https://gitlab.com/cordite/network-map-service) to orchestrate the communication between the nodes.

Additionally, all `party` nodes will include:

* If `directory` is a Gradle project, any CorDapps defined in the project’s source folders and its dependencies.
* If `directory` is *not* a Gradle project, all JAR files containing CorDapps found in `directory` and its sub-directories.

This means that running the network from the generated sample CorDapp from `web3j-corda new`, for example, 
would automatically build and add the sample CorDapp to each node.

## Configuration

The configuration values available in the DSL are as follows:

### **`network`**

Defines a dockerized Corda network.

#### Optional configuration

* `version: org.web3j.corda.util.OpenApiVersion`
    
    Open API version exposed by the nodes (defaults to `3.0.1`). 
    
* `directory: java.io.File`
    
    Base directory where CorDapps are located (defaults to `user.dir` system property).

* `tag: String`
    
    Network Map Docker image tag (defaults to `v0.5.0`)

#### Read-only configuration

* `image: String`
    
    Network map Docker image name, `cordite/network-map`.
    
* `notaries: Array<CordaNotaryNode>`
    
    The notary nodes in this network, indexed by position (e.g. `notaries[0]`).

* `parties: Array<CordaPartyNode>`
    
    The party nodes in this network, indexed by position (e.g. `parties[0]`).
    
* `api: org.web3j.corda.networkmap.NetworkMapApi`

    The API to interact with the Network Map in this network. 

* `service: org.web3j.corda.protocol.CordaService`

    The Corda service to access the Network Map.

### **`notary`**, **`party`**

Inside a `network` you can declare nodes using the `notary` and `party` blocks.
Both have in common the following configuration values:

#### Required configuration

* `name: String`

    The legal identity name of the Corda node.
    (see [myLegalName](https://docs.corda.net/corda-configuration-file.html#corda-configuration-file-mylegalname)),
    e.g. `name = "O=PartyA, L=London, C=GB"`

#### Optional configuration

In addition, each node can specify the following properties:

* `p2pPort: Int`

    Corda P2P port for this node, random by default.
    (see [p2pAddress](https://docs.corda.net/corda-configuration-file.html#corda-configuration-file-p2paddress)),
    e.g. `10006`
   
* `rpcSettings { <config> }`
    
    Specifies RPC settings for the node.
    (see [rpcSettings](https://docs.corda.net/corda-configuration-file.html#corda-configuration-file-rpc-settings)),
    e.g. (ports are randomized by default)
```kotlin
rpcSettings {
    port = 10006
    adminPort = 10026
}
```

* `rpcUsers { <config> }` 
    
    Set the RPC users for this node.
    (see [rpcUsers](https://docs.corda.net/corda-configuration-file.html#corda-configuration-file-rpc-users)),
    e.g. (showing default values)
```kotlin
rpcUsers {
    user = "user1"
    password = "test"
    permissions = "ALL"
}
```

* `autoStart: Boolean`

    Set if this node should start automatically (`true` by default).
    
* `timeOut: java.util.time.Duration`

    Timeout for this Corda node to start (defaults to 5 min).
    
* `image: String`
    
    Docker image name, e.g. `corda-zulu-4.1`

* `tag: String`

    Docker image tag, e.g. `latest`
    
#### Read-only configuration

* `rpcSettings.address: String`
    
    External host and port for the RPC server binding, e.g. `example.com:10002`
    
* `rpcSettings.adminAddress: String`
    
    External host and port for the RPC admin binding, e.g. `example.com:10002`

* `p2pAddress: String`

    Address/port the node uses for inbound communication from other nodes. 
    (see [p2pAddress](https://docs.corda.net/corda-configuration-file.html#corda-configuration-file-p2paddress))
     e.g. `example.com:10002`

* `canonicalName: String`

    Canonical name to be used as Docker container and host name, e.g. `notary-london-gb`.

### **`notary`**

Defines a Corda notary node.

#### Optional configuration

* `validating: Boolean`

    Defines this notary as validation (defaults to `false`).

### **`party`**

Defines a Corda party node.

#### Optional configuration

* `webPort: Int`

    Braid server port for this node, random by default.

#### Read-only configuration

* `webAddress: String`

    Address/port to communicate with Braid server, e.g. `example.com:10002`
    
* `corda: org.web3j.corda.protocol.Corda`

    Corda instance to interact with this node through the Braid server.

## Customizing Docker images and tags

All of `network`, `notary` and `party` elements allow you to specify which Docker container image name and tag you want
to use in your network. This table specifies the default values used:

| Marker            |       Image name      |      Image tag      |
| ------------------|:---------------------:| -------------------:|
| `network`         | `cordite/network-map` |       `v0.5.0`      |
| `notary`, `party` | `corda/corda-zulu-4.1`|       `latest`      |
