Quick start
===========

web3j-corda CLI
---------

Install the web3j-corda binary.

To get the latest version on Mac OS or Linux, type the following in your terminal:

```bash
curl -L https://getcorda.web3j.io | sh
```

Then to create a new project, simply run:

```zsh
web3j-corda new -o ~/template/cordapp -n Sample -p <package-name>
```

Or, to generate client wrappers for an existing CorDapp, run:

```zsh
web3j-corda generate -d <path-existing-cordapp> -o <output-dir> -p <package-name>
```

Then to build your project run:

```zsh
./gradle build
```

For more information on using the web3j-corda CLI, head to the [CLI section](command_line_tools.md).

A [web3j-corda sample project](https://gitlab.com/web3j/corda-samples) is available that demonstrates a number of core features of web3j-corda, including:

* Generate CorDapp client wrappers for deployed CorDapps.
* Interact with a CorDapp listing its nodes and starting flows.
* Generate automated tests using Docker containers to verify the working of CorDapp. 
