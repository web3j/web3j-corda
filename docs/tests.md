Creating test networks
======================

Web3j Corda provides a DSL that allows you to automatically generate and configure a set of nodes for testing
(in a way, is quite similar to the [`CordForm`](https://docs.corda.net/generating-a-node.html#the-cordform-task) Gradle plugin). 
Here is an example of a Corda network that creates three nodesand start them on Docker containers:
```kotlin
val network = network {
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

* A `Notary` node that:
    * Offers a validating notary service.
    * Is running the CorDapps located under the `user.dir` system property, either as JAR files or Gradle projects.
* `PartyA` and `PartyB` nodes that:
    * Are not offering any services.
    * Are running the CorDapps located under the `user.dir` system property, either as JAR files or Gradle projects.
    * Have an RPC user, `user1` by default, that will be used by Braid to log into the node via RPC.
* For each party node, a Braid server exposing their CorDapps using Open API.
* A network map to orchestrate the communication between the nodes.

```
You can then access the nodes in the network by its index in your test code:
```kotlin
network.notaries[0] // Returns the Notary node
network.parties[0] // Returns the PartyA node
```

### Customizing node properties

In addition to the `name`, each node can specify the following properties:

* `p2pPort`: Corda P2P port for this node.
* `rpcPort`: Corda RPC port for this node.
* `adminPort`: Admin port for this Corda node.
* `autoStart`: Start this node automatically?
* `timeOut`: Timeout for this Corda node to start.

### Customizing Docker images and tags

All of `network`, `notary` and `party` elements allow you to specify which Docker container image and tag you want to
use for your network. In this table you can see the default values:

| Element           |       Image name      |      Image tag      |
| ------------------|:---------------------:| -------------------:|
| `network`         | `cordite/network-map` |       `v0.5.0`      |
| `notary`, `party` | `corda/corda-zulu-4.1`|       `latest`      |


