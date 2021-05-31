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
import com.github.jk1.license.ManifestData
import com.github.jk1.license.ModuleData
import com.github.jk1.license.util.Files

class ManifestLicenseConverter {
    static Set<License> getManifestLicenses(final ModuleData module) {
        return module.manifests.collect(){readManifestLicense(it)}.findAll()
    }

    private static License readManifestLicense(final ManifestData manifestData) {
        if (manifestData.license) {
            String licenseName = manifestData.license
            String licenseUrl
            if (Files.maybeLicenseUrl(manifestData.getLicenseUrl())) {
                licenseUrl = manifestData.getLicenseUrl()
            } else if (manifestData.isHasPackagedLicense()) {
                licenseUrl = manifestData.getUrl()
            } else {
                licenseUrl = null
            }
            return new License(licenseName, licenseUrl)
        }
        return null
    }
}
