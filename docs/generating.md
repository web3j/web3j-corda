Generating web3j client wrappers
================================

There are two ways to generate web3j client wrappers for given CorDapps.

1. Using command-line tool
2. Using gradle task

## Using command-line tool

Please refer to [command line tools](command_line_tools.md)

## Using gradle task

We should add the task to generate the CorDapp client wrappers using the following:

```groovy
task generateCorDappWrappers(type: JavaExec, group: 'corda') {
    classpath = sourceSets.main.runtimeClasspath
    main = 'org.web3j.corda.console.CordaCommandMain'

    args 'generate', '--package', 'org.web3j.corda',
            '--cordappsDir', "$projectDir",
            '--outputDir', "$projectDir/clients"
}
```

It exposes a gradle task `generateCorDappWrappers` which can be invoked to generate client wrappers for the CorDapps.
