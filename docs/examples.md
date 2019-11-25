Examples
========

### Creating a template CorDapp and client wrappers

[Install](quickstart.md) the `web3j-corda` CLI. 

Run the following command :

```shell
$ web3j-corda new -n Sample -o <destDir> -p org.web3j.corda
```

This will create a new template Kotlin CorDapp in `<destDir>`, with client wrappers generated in `<destDir>/clients`.

The business logic for the flow can be added in the following file:

- `workflows/src/main/kotlin/org/web3j/corda/flows/Flows.kt`

The flow state can be found in:

- `contracts/src/main/kotlin/org/web3j/corda/states/SampleState.kt`

The contract file can be found in:

- `contracts/src/main/kotlin/org/web3j/corda/contracts/SampleContract.kt`

The template integration test can be found at:

- `clients/src/test/kotlin/org/web3j/corda/workflows/api/WorkflowsTest.kt`

In the template integration test you'll find a defined network, such as the following:

```kotlin
network = network {
    directory = File(System.getProperty("user.dir")).parentFile
    nodes {
        party {
            name = "O=PartyA, L=London, C=GB"
        }
    }
}
```

Once you've added business logic for the flow and made any changes you wish to make to the contracts and states, 
you need to run the test (the client will be regenerated automatically):

```shell
$ ./gradlew test
```

### Generating client wrappers for existing CorDapps

[Install](quickstart.md) the `web3j-corda` executable. 

Provided we have the `Obligation` CorDapp at the location `~/obligation`, we can execute the following command to generate client wrappers: 

```shell
$ web3j-corda generate -d ~/obligation -o ~/obligation/clients -p org.test.web3j.corda
```

This will generate the client wrappers in the provided output directory `~/obligation/clients`.
