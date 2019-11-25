Connect to a Corda Node
=======================

Initialise a connection, and create our Corda client service:

```kotlin
val service = CordaService("http://localhost:9000/") // URL exposed by Corda OpenAPI connector
val corda = Corda.build(service)
```

To print all the nodes connected to the current node:

```kotlin
val nodes = corda.api.network.nodes.findAll()
```

To query the list of all running CorDapps:

```kotlin
val corDapps = corda.api.corDapps.findAll()
```

To start a flow there are two options depending on whether you want to use a generated CorDapp wrapper or just the Corda API directly:

### Using the Web3j CorDapp wrapper

Please refer on how to use client wrappers in a [type-safe way](usage.md)

### Using Corda API

Due to the lack of type-safety, this is not recommended as it can lead to runtime exceptions:

```kotlin
// Initialise the parameters of the flow 
val params = InitiatorParameters("$1", "O=PartyA, L=London, C=GB", false)

val issue = corda.api
    .corDapps.findById("obligation")
    .flows.findById("issue-obligation")

// Type-conversions with potential runtime exception!
var signedTx = issue.start(params).convert<SignedTransaction>()
```
