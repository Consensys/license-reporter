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
package tech.pegasys.internal.license.reporter;

import com.github.jk1.license.ProjectData;
import com.github.jk1.license.render.ReportRenderer;

import java.io.File;
import java.util.Map;

public class GroupedLicenseHtmlRenderer implements ReportRenderer {
  public static final String FILE_NAME_PARAM = "fileName";
  public static final String PROJECT_NAME_PARAM = "projectName";
  public static final String LICENSE_OVERRIDE_PARAM = "licenseOverrideFile";
  public static final String MULTI_LICENSE_PARAM = "multiLicensesFile";

  public GroupedLicenseHtmlRenderer(final Map<String, Object> params) {
    this(
            (String) params.getOrDefault(FILE_NAME_PARAM, "index.html"),
            (String) params.getOrDefault(PROJECT_NAME_PARAM, null),
            (File) params.getOrDefault(LICENSE_OVERRIDE_PARAM, null),
            (File) params.getOrDefault(MULTI_LICENSE_PARAM, null));
  }

  public GroupedLicenseHtmlRenderer(
          final String fileName,
          final String projectName,
          final File licenseOverrideFile,
          final File multiLicensesFile) {

  }


  @Override
  public void render(final ProjectData data) {
    final org.gradle.api.Project project = data.getProject();
  }
}
