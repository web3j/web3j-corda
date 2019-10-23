Generating web3j client wrappers
================================

There are two ways to generate web3j client wrappers for given CorDapps.

1. Using the CLI
2. Using a Gradle task

## Using command-line tool

Please refer to the [CLI](command_line_tools.md) section.

## Using a Gradle task

We should add the task to generate the CorDapp client wrappers using the following:

```groovy
task generateCorDappWrappers(type: JavaExec, group: 'web3j') {
    classpath = sourceSets.main.runtimeClasspath
    main = 'org.web3j.corda.console.CordaCommandMain'

    args 'generate', 
            '--package-name', project.group,
            '--cordapps-dir', "$projectDir",
            '--output-dir', "$projectDir/clients"
}
```

It exposes a Gradle task `generateCorDappWrappers` which can be invoked to generate client wrappers for the CorDapps.
