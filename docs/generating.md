Generating web3j client wrappers
================================

There are two ways to generate web3j client wrappers for given CorDapps.

1. Using command-line tool
2. Using gradle task

## Using command-line tool

// link to command line tool, generate

## Using gradle task

We should add the dependency to import `web3-corda` to the CorDapp using the following:

```groovy
dependencies {
    implementation 'org.web3j:web3j-corda-core:0.1.0-SNAPSHOT'
}
```

It exposes a gradle task `generateCorDappWrappers` which can be invoked to generate client wrappers for the CorDapps 
in the folder `$projectDir/src/test/resources/cordapps` -> this has to be changed!
