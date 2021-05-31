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

import tech.pegasys.internal.license.reporter.converters.BundledLicenseConverter
import tech.pegasys.internal.license.reporter.converters.ManifestLicenseConverter
import tech.pegasys.internal.license.reporter.converters.OverriddenLicenseConverter

import com.github.jk1.license.License
import com.github.jk1.license.ModuleData

class LicenseConverter {
    static Map<ModuleData, Set<String>> convertToLicenseStrings(final Set<ModuleData> moduleData, List<OverriddenLicense> overriddenLicenses) {
        final Map<ModuleData, Set<String>> moduleLicenses = [:] as TreeMap
        moduleData.each {
            Set<License> licenses = (tech.pegasys.internal.license.reporter.converters.ManifestLicenseConverter.getManifestLicenses(it) + tech.pegasys.internal.license.reporter.converters.PomLicenseConverter.getPomLicenses(it)) ?: tech.pegasys.internal.license.reporter.converters.BundledLicenseConverter.getBundledLicenses(it)

            Set<String> licenseNames = tech.pegasys.internal.license.reporter.converters.OverriddenLicenseConverter.applyOverriddenLicense(licenses, it, overriddenLicenses)

            // report Unknown or multiple licenses
            if (!licenseNames) {
                printf("Module %s has no license information available. Consider adding module license information in overrideLicenses.%n", moduleName(it))
                // categorize it under Unknown
                licenseNames += ["Unknown"]
            } else if (licenseNames.size() > 1) {
                printf("Module %s has multiple license reported:%n%s%nConsider adding module license information in overrideLicenses.%n%n", moduleName(it), licenseNames.join('\n'))
            }

            moduleLicenses.put(it, licenseNames)
        }

        return moduleLicenses
    }

    static Map<String, Set<ModuleLicenseInformation>> groupByLicense(Map<ModuleData, Set<String>> moduleLicenses, List<OverriddenLicense> overriddenLicenses) {
        Map<String, Set<ModuleLicenseInformation>> licenseGroup = new TreeMap<>()

        moduleLicenses.each {
            String projectUrl = projectUrl(it.key)
            Set<License> licenses = (ManifestLicenseConverter.getManifestLicenses(it.key) + tech.pegasys.internal.license.reporter.converters.PomLicenseConverter.getPomLicenses(it.key)) ?: tech.pegasys.internal.license.reporter.converters.BundledLicenseConverter.getBundledLicenses(it.key)

            // if licenses are empty, try to fetch it from overriddenLicense section for this module - if any
            if (!licenses) {
                OverriddenLicense overriddenLicense = OverriddenLicenseConverter.getModuleOverriddenLicense(it.key, overriddenLicenses)
                if (overriddenLicense) {
                    licenses.add(new License(overriddenLicense.moduleLicense, overriddenLicense.moduleLicenseUrl))
                }
            }

            // get bundled files url
            Set<String> bundledLicenseFiles = BundledLicenseConverter.getBundledUrl(it.key)

            ModuleLicenseInformation licenseInformation =  new ModuleLicenseInformation(it.key.group, it.key.name, it.key.version, projectUrl, licenses, bundledLicenseFiles)

            it.value.each {
                Set<ModuleLicenseInformation> moduleSet = licenseGroup.computeIfAbsent(it, k -> new HashSet<ModuleLicenseInformation>())
                moduleSet.add(licenseInformation)
            }

        }

        return licenseGroup
    }

    static String projectUrl(ModuleData it) {
        String manifestProjectUrl = it.manifests.find().url
        String pomProjectUrl = it.poms.find().projectUrl
        if (manifestProjectUrl) {
            return manifestProjectUrl
        } else if(pomProjectUrl) {
            return pomProjectUrl
        }
        return ''
    }

    static String moduleName(ModuleData module) {
        "${module.group}:${module.name}:${module.version}"
    }

}
