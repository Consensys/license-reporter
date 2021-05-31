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
package tech.pegasys.internal.license.reporter.converters

import tech.pegasys.internal.license.reporter.OverriddenLicense

import com.github.jk1.license.License
import com.github.jk1.license.ModuleData

class OverriddenLicenseConverter {
    static Set<String> applyOverriddenLicense(Set<License> licenses, ModuleData module, List<OverriddenLicense> overriddenLicenses) {
        OverriddenLicense overriddenLicense = getModuleOverriddenLicense(module, overriddenLicenses)
        return overriddenLicense ? [overriddenLicense.moduleLicense] : licenses.collect {it.name}
    }

    static OverriddenLicense getModuleOverriddenLicense(final ModuleData moduleData, List<OverriddenLicense> overriddenLicenses) {
        return overriddenLicenses.find {isDependencyMatchesAllowedLicense(moduleData, it) && it.moduleLicense}
    }

    private static boolean isDependencyMatchesAllowedLicense(ModuleData moduleData, OverriddenLicense overriddenLicense) {
        return isModuleGroupNameMatchesOverriddenLicense(moduleData, overriddenLicense) &&
                isModuleNameMatchesOverriddenLicense(moduleData, overriddenLicense) &&
                isModuleVersionMatchesOverriddenLicense(moduleData, overriddenLicense)
    }

    private static boolean isModuleGroupNameMatchesOverriddenLicense(ModuleData moduleData, OverriddenLicense overriddenLicense) {
        def (group, name) = overriddenLicense.moduleName.tokenize(':')
        return moduleData.group ==~ group || group == null || moduleData.group == group
    }

    private static boolean isModuleNameMatchesOverriddenLicense(ModuleData moduleData, OverriddenLicense overriddenLicense) {
        def (group, name) = overriddenLicense.moduleName.tokenize(':')
        return moduleData.name ==~ name || name == null || moduleData.name == name
    }

    private static boolean isModuleVersionMatchesOverriddenLicense(ModuleData moduleData, OverriddenLicense overriddenLicense) {
        return moduleData.version ==~ overriddenLicense.moduleVersion || overriddenLicense.moduleVersion == null ||
                moduleData.version == overriddenLicense.moduleVersion
    }
}
