Examples
========

### Creating a template CorDapp and client wrappers

[Install](quickstart.md) the `web3j-corda` CLI. 

Run the following command :

```shell
$ web3j-corda new -n Sample -o ~/corda/gen -p org.test.web3j.corda
```

### Generating client wrappers for existing CorDapps

[Install](quickstart.md) the `web3j-corda` executable. 

Provided we have the `Obligation` CorDapp at the location `~/obligation`, we can execute the following command to generate client wrappers: 

```shell
$ web3j-corda generate -d ~/obligation -o ~/obligation/clients -p org.test.web3j.corda
```
