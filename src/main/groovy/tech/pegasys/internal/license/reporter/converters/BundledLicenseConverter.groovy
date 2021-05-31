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

import com.github.jk1.license.License
import com.github.jk1.license.ModuleData

class BundledLicenseConverter {

    static Set<License> getBundledLicenses(final ModuleData moduleData) {
        moduleData.licenseFiles*.fileDetails.flatten().findAll {it.license}.collect {new License(it.license, it.licenseUrl)}.toSet()
    }

    static Set<String> getBundledUrl(final ModuleData moduleData) {
        moduleData.licenseFiles*.fileDetails.flatten().findAll {it.file}.collect {it.file}.toSet()
    }
}
