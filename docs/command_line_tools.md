Command line tools
==================

A web3j-corda fat jar is distributed with each release providing command line tools. 
The following functionality of web3j-corda is exposed from the command line:

* Generate a template CorDapp project and the respective client wrappers
* Generate client wrappers for existing CorDapps

### Using web3j-corda new command

To generate a template CorDapp project along with the client wrappers: 

```
$ web3j-corda new --name=<corDappName> --output-dir=<output-dir> --package-name=<packageName>
```

### Using web3j-corda generate command

To generate a web3j-corda client wrappers for existing CorDapps: 

```
|$ web3j-corda generate (--url=<openApiUrl> | --cordapps-dir=<corDapps-dir>) --output-dir=<output-dir> --package-name=<packageName>
```
