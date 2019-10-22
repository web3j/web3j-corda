Network Queries
=======================

- To query all the nodes connected to the current node:

```kotlin
corda.api.network.nodes.findAll()
```

- To query nodes with given X500 name:

```kotlin
corda.api.network.nodes.findByX500Name("O=PartyB,L=New York,C=US")[0]
```

- To query nodes with given host and port:

```kotlin
corda.api.network.nodes.findByHostAndPort("party-new-york-us:8080")[0]
```

- To query all connected notaries:

```kotlin
corda.api.network.notaries.findAll()
```