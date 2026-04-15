# License Reporter

A custom license renderer used by Consensys [Teku](https://github.com/Consensys/teku) and [Web3Signer](https://github.com/Consensys/web3signer) projects for the [Gradle License Report](https://github.com/jk1/Gradle-License-Report) plugin.

The renderer `GroupedLicenseHtmlRenderer` generates a simple HTML which groups dependencies under their license.

In the build logs, it will report the dependencies with no license information or multiple licenses. To avoid reporting
such dependencies in multiple license groups, they can be added in the `allowedLicensesFile` by adding
`overrideLicenses` section.

```json
...
"overrideLicenses" : [
    {
      "moduleName": "com.github.fge",
      "moduleLicense": "Apache License, Version 2.0"
    },
      ...
    {
      "moduleName": "io.libp2p:jvm-libp2p-minimal",
      "moduleLicense": "Apache License, Version 2.0",
      "moduleLicenseUrl": "https://github.com/libp2p/jvm-libp2p/blob/develop/LICENSE-APACHE"
    },
```

## Usage
- Add dependency in buildscript
```groovy
buildscript {
  dependencies {
    classpath 'io.consensys.protocols:license-reporter:<version>'
  }
}
```
- Add `GroupedLicenseHtmlRenderer` in list of renderers:

```groovy
licenseReport {
    configurations = ['runtimeClasspath']
    outputDir = "${buildDir}/reports/licenses"
    projects = [project] + project.subprojects.findAll {it.name != "errorprone-checks" }
    excludes = ['org.junit:junit-bom']
    allowedLicensesFile = new File("${rootDir}/gradle/license-report-config/allowed-licenses.json")
    filters = [new LicenseBundleNormalizer()]
    renderers = [new io.consensys.protocols.license.reporter.GroupedLicenseHtmlRenderer()]
}
```
