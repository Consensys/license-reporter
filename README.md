License Reporter
================

A custom license renderer `GroupedLicensesHtmlRenderer` that can be used by [Gradle License Report](https://github.com/jk1/Gradle-License-Report) plugin.

This renderer generates a simple HTML which groups dependencies under their license. 

In the build logs. it will report the dependencies with no license information or multiple licenses. To avoid reporting 
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
  repositories {
    maven { url "https://artifacts.consensys.net/public/maven/maven/" }
  }
  dependencies {
    classpath 'tech.pegasys.internal.license.reporter:license-reporter:develop'
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
    renderers = [new tech.pegasys.internal.license.reporter.GroupedLicenseHtmlRenderer()]
}
```