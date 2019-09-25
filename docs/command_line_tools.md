Command line tools
==================

A web3j-corda fat jar is distributed with each release providing command line tools. 
The following functionality of web3j-corda is exposed from the command line:

* Generate a template CorDapp project and the respective client wrappers
* Generate client wrappers for existing CorDapps

### Using web3j-corda new command

To generate a template CorDapp project with the client wrappers: 

``` bash
web3j-corda new -n=<corDappName> -o=<outputDir> -p=<packageName>
```

### Using web3j-corda generate command

To generate a web3j-corda client wrappers for existing CorDapps: 

``` bash
web3j-corda generate (-u=<openApiUrl> | -d=<corDappsDir>) -o=<outputDir> -p=<packageName>
```
