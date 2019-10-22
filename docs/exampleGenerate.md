Example Generating Client wrappers for existing CorDapps
========================================================

[Install](quickstart.md) the `web3j-corda` executable. 

Provided we have the `Obligation` CorDapp at the location `~/obligation`, we can execute the following command
to generate client wrappers: 

```zsh
$ web3j-corda generate -d ~/obligation -o ~/obligation/clients -p org.test.web3j.corda
```
