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

import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.gradle.api.InvalidUserDataException

class OverrideLicenseFileReader {
    static List<OverriddenLicense> importOverriddenLicenses(Object overrideLicensesFile) {
        def slurpResult
        if(overrideLicensesFile instanceof File ) {
            slurpResult = new JsonSlurper().setType(JsonParserType.LAX).parse(overrideLicensesFile)
        } else if(overrideLicensesFile instanceof URL) {
            slurpResult = new JsonSlurper().setType(JsonParserType.LAX).parse(overrideLicensesFile)
        } else if(overrideLicensesFile instanceof String) {
            def source
            try {
                source = new URL(overrideLicensesFile)
            } catch (MalformedURLException ignored) {
                source = new File(overrideLicensesFile)
            }
            return importOverriddenLicenses(source)
        } else {
            throw new InvalidUserDataException("Unknown type for overrideLicensesFile: " + overrideLicensesFile.getClass())
        }
        return slurpResult.overrideLicenses.collect { new OverriddenLicense(it.moduleName, it.moduleVersion, it.moduleLicense, it.moduleLicenseUrl) }
    }
}
