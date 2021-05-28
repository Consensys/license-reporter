License Reporter
================

A custom license renderer `GroupedLicensesHtmlRenderer` that can be used by [Gradle License Report](https://github.com/jk1/Gradle-License-Report) plugin.

This renderer generates a simple HTML which groups dependencies under their license. It will fail to render if it find dependencies with multiple licenses. You can specify which license to use for these dependencies in `multiLicensesFile`.

The dependencies which does not report any license will be categorized under 'Unknown'. The license information for these dependencies can
be overridden by specifying it in a `licenseOverrideFile` file.

## Usage

```groovy
licenseReport {
  configurations = ['runtimeClasspath']
  outputDir = "${buildDir}/reports/licenses"
  projects = [project] + project.subprojects.stream().filter(project -> !project.name.equals("errorprone-checks")).collect(Collectors.toList())
  excludes = ['org.junit:junit-bom']
  allowedLicensesFile = new File("${rootDir}/gradle/license-report-config/allowed-licenses.json")
  filters = [
          new LicenseBundleNormalizer(["bundlePath": new File("${rootDir}/gradle/license-report-config/license-normalizer.json"), "createDefaultTransformationRules": true])
  ]
  renderers = [
          new GroupedLicensesHtmlRenderer(["projectName":"Teku",
                                           "multiLicensesFile": new File("${rootDir}/gradle/license-report-config/multi-licenses.txt"),
                  "licenseOverrideFile": new File("${rootDir}/gradle/license-report-config/override-licenses.txt")
          ])
  ]
}
```