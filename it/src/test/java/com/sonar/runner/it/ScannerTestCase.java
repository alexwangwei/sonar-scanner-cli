/*
 * SonarSource :: IT :: SonarQube Scanner
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.runner.it;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.version.Version;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ScannerTestCase {

  private static final Logger LOG = LoggerFactory.getLogger(ScannerTestCase.class);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @ClassRule
  public static Orchestrator orchestrator = SonarScannerTestSuite.ORCHESTRATOR;

  private static Version artifactVersion;

  private static Version artifactVersion() {
    if (artifactVersion == null) {
      String scannerVersion = System.getProperty("scanner.version");
      if (StringUtils.isNotBlank(scannerVersion)) {
        LOG.info("Use provided Scanner version: " + scannerVersion);
        artifactVersion = Version.create(scannerVersion);
      } else {
        try (FileInputStream fis = new FileInputStream(new File("../target/maven-archiver/pom.properties"))) {
          Properties props = new Properties();
          props.load(fis);
          artifactVersion = Version.create(props.getProperty("version"));
          return artifactVersion;
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }
    }
    return artifactVersion;
  }

  SonarScanner newScanner(File baseDir, String... keyValueProperties) {
    SonarScanner scannerCli = SonarScanner.create(baseDir, keyValueProperties);
    scannerCli.setScannerVersion(artifactVersion().toString());
    return scannerCli;
  }

}
