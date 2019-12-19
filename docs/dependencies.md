Dependencies
============

To resolve all web3j-corda relevant dependencies, add the following to your project build file:

### Maven

```xml
<dependency>
    <groupId>org.web3j.corda</groupId>
    <artifactId>web3j-corda-core</artifactId>
    <version>0.2.8</version>
</dependency>
```

### Gradle

```groovy
dependencies {
    implementation 'org.web3j.corda:web3j-corda-core:0.2.8'
}
```

To use the Web3j Corda test module, you'all also need to include it in your project build file:
    
### Maven
  
```xml
<dependency>
    <groupId>org.web3j.corda</groupId>
    <artifactId>web3j-corda-test</artifactId>
    <version>0.2.8</version>
</dependency>
```

### Gradle

```groovy
dependencies {
    testImplementation "org.web3j.corda:web3j-corda-test:0.2.8"
}
```