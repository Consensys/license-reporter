/*
 * Copyright 2021 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.internal.license.reporter

import static tech.pegasys.internal.license.reporter.LicenseConverter.convertToLicenseStrings
import static tech.pegasys.internal.license.reporter.LicenseConverter.groupByLicense

import java.time.LocalDateTime

import com.github.jk1.license.LicenseReportExtension
import com.github.jk1.license.ModuleData
import com.github.jk1.license.ProjectData
import com.github.jk1.license.render.ReportRenderer
import groovy.xml.MarkupBuilder
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Input

class GroupedLicenseHtmlRenderer implements ReportRenderer {
  private String fileName
  private File outputFile
  private boolean includeTimestamp

  GroupedLicenseHtmlRenderer(String fileName = 'index.html', boolean includeTimestamp = true) {
    this.fileName = fileName
    this.includeTimestamp = includeTimestamp
  }

  @Input
  String getFileNameCache() { return this.fileName }


  @Override
  void render(final ProjectData data) {
    final Project project = data.project
    final LicenseReportExtension config = project.licenseReport
    if (config == null) {
      throw new GradleException("LicenseReportExtension is not available")
    }
    outputFile = new File(config.outputDir, fileName)

    final List<OverriddenLicense> overriddenLicenses =
            OverrideLicenseFileReader.importOverriddenLicenses(config.allowedLicensesFile)

    // read all module licenses. they can appear in pom, manifest and/or embedded
    Map<ModuleData, Set<String>> moduleLicenses = convertToLicenseStrings(data.allDependencies, overriddenLicenses)

    // group them based on license string
    Map<String, Set<ModuleLicenseInformation>> licenseGroup = groupByLicense(moduleLicenses, overriddenLicenses)

    writeHtmlFile(project, licenseGroup, outputFile, includeTimestamp)
  }

  private static void writeHtmlFile(project, licenseGroup, File outputFile, boolean includeTimestamp) {
    def version = project.version != "unspecified" ? "(${project.version})" : ""
    def writer = new StringWriter()
    def builder = new MarkupBuilder(writer)
    builder.mkp.yieldUnescaped("<!DOCTYPE html>")
    builder.html {
      head {
        title "Dependency License Report for ${project.name}"
      }
      body(style: 'font-family: sans-serif') {
        h1 "Dependency License Report for ${project.name} ${version}"
        table(style: 'width:100%;text-align:left;border-collapse:separate;background-color:LightGrey;') {
          tbody {
            licenseGroup.eachWithIndex { entry, index ->
              tr {
                td {
                  h3("${index + 1}. ${entry.key}")
                }
              }

              entry.value.sort().each { licenseInfo ->
                tr {
                  td(style: 'width: 98.0679%%;border: 10px solid LightGrey; background-color:white;padding: 10px;') {
                    p {
                      strong "Group: "
                      mkp.yield "${licenseInfo.group}"
                      strong "Name: "
                      mkp.yield "${licenseInfo.name}"
                      strong "Version: "
                      mkp.yield "${licenseInfo.version}"
                    }
                    if (licenseInfo.projectUrl) p {
                      strong "Project URL:"
                      a(href: "${licenseInfo.projectUrl}", "${licenseInfo.projectUrl}")
                      br()
                    }
                    if (licenseInfo.license) p {
                      strong "Licences: "
                      br()
                      ul {
                        licenseInfo.license.sort().each { license ->
                          li {
                            code "${license.name}"
                            if (license.url) code {
                                mkp.yield " - "
                                a(href: "${license.url}", "${license.url}")
                              }
                          }
                        }
                      }
                    }
                    if (licenseInfo.embedded) p {
                      strong "Embedded: "
                      ul {
                        licenseInfo.embedded.sort().each { embedded ->
                          li {
                            code {a(href: "${embedded}", "${embedded}")}
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        if (includeTimestamp) {
          br()
          p "Report Generated at: ${LocalDateTime.now()}"
        }
      }
    }


    outputFile.write writer.toString()
  }
}
