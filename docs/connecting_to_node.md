Connect to a Corda Node
=======================

To print all the nodes connected to the current node:

```kotlin
val service = CordaService("http://localhost:9000/") // URL exposed by BRAID service
val corda = Corda.build(service)
corda.api.network.nodes.findAll()
```

To query the list of all running CorDapps:

```kotlin
val service = CordaService("http://localhost:9000/") // URL exposed by BRAID service
val corda = Corda.build(service)
corda.api.corDapps.findAll()
```

To start a flow there are two options depending on whether you want to use a generated CorDapp wrapper
or just the Corda API directly:

### Using Corda API
This way works but is not type-safe, so can lead to runtime exceptions:
```kotlin
// Initialise the parameters of the flow 
val params = InitiatorParameters("$1", "O=PartyA, L=London, C=GB", false)

val issue = corda.api
    .corDapps.findById("obligation")
    .flows.findById("issue-obligation")

// Type-conversions with potential runtime exception!
var signedTx = issue.start(parameters).convert<SignedTransaction>()
```

### Using the web3j CorDapp wrapper

Please refer on how to use client wrappers in a [type-safe way](usage.md)