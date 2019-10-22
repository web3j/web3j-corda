Command line utility
====================

The web3j-corda CLI makes it easy to:

* Generate a template CorDapp project and the respective client wrappers
* Generate client wrappers for existing CorDapps

You can install the web3j-corda CLI by running the following command in your terminal:

```shell
curl -L https://getcorda.web3j.io | bash
```

### Create a template CorDapp project

To generate a template CorDapp project with the client wrappers: 

```shell
web3j-corda new --name=<corDappName> --output-dir=<output-dir> --package-name=<packageName>
```
### Create CorDapp client wrappers

To generate a web3j-corda client wrappers to existing CorDapps: 

```shell
web3j-corda generate (--url=<openApiUrl> | --cordapps-dir=<corDapps-dir>) --output-dir=<output-dir> --package-name=<packageName>
```
