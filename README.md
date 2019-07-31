web3j integration for Corda
===========================

## Introduction

web3j-corda is a lightweight client library for working with Cordapps and interacting with different nodes on Corda network.

## Features
* Connect to a Corda node.
* Query the available CorDapps in the node
* Interact with the deployed CorDapp
* Generate automated tests using Docker containers to verify the working of CorDapp. 

## Quickstart

A [web3j-corda sample project](https://gitlab.com/web3j/corda-samples) is available that demonstrates a number of core features of Cordapp using web3j-corda, including:
* Interact with a CorDapp
* Generate automated tests using Docker containers to verify the working of CorDapp. 

## Getting started

Add the relevant dependancy to your project 

### Maven

```xml
   <dependency>
     <groupId>org.web3j</groupId>
     <artifactId>web3j-corda</artifactId>
     <version>1.0.0</version>
   </dependency>
```

### Gradle

```groovy
dependencies {
    compile ('org.web3j:web3j-corda:1.0.0')
}
```

## Connect to a Corda Node

To print all the nodes connected to the current node: 

```kotlin
val service = CordaService("http://localhost:9000/") // URL exposed by BRAID service
val corda = Corda.build(service)
corda.network.nodes.forEach { println(it) }
```

To query the list of all running CorDapps:

```kotlin
val service = CordaService("http://localhost:9000/") // URL exposed by BRAID service
val corda = Corda.build(service)
val corDapps = corda.corDapps

```

To start a flow:

1. Hardcoded way which is not typesafe. Can lead to runtime exceptions
    ```kotlin
    var signedTx: SignedTransaction = corda.corDapps["obligation-cordapp"]
        .flows.start("issue-obligation", party) as SignedTransaction
    ```

2. Better looking hardcoded execution, again not type safe. 
    ```kotlin
    var signedTx = corda.corDapps["obligation-cordapp"]
        .flows["issue-obligation"].start(party)
    ```

3. web3j-corda generated version. It is typesafe. 
    ```kotlin
    val service = CordaService("http://localhost:9000/") // URL exposed by BRAID service
    val corda = Corda.build(service)
    
    // Initialise the parameters of the flow 
    val party = Party("PartyA", owningKey)
    
    // Invoke the flow
    Obligation.Issue.build(corda).start(party, false)
    ```


