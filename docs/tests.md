Writing tests
=============

Define a test network

In a similar way as you can define CordForm networks to test your CordDapp flows,

```kotlin
val network = network {
    nodes {
        notary {
            name = "O=Notary, L=London, C=GB"                                         
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

You can the access the nodes in the network by its index:

```kotlin
network.notaries[0] // Returns the Notary node
network.parties[0] // Returns the PartyA node
```