/*
 * Copyright 2020 Matthias Hanisch (reallyinsane)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mathan.trainsimulator.service.jni;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Default implementation for production mode loading {@link NativeLibrary} via JNI and accessing the Windows DLL.
 */
@Component
@Profile("native")
public class NativeLibraryFactoryImpl implements NativeLibraryFactory, InitializingBean {

  private Logger logger = LoggerFactory.getLogger(getClass());

  public static final String REGISTRY_KEY = "SOFTWARE\\WOW6432Node\\railsimulator.com\\railworks";
  public static final String REGISTRY_VALUE = "install_path";
  private static NativeLibrary instance = null;
  private final NativeConfiguration configuration;

  public NativeLibraryFactoryImpl(NativeConfiguration configuration) {

    this.configuration = configuration;
  }

  public static String getDllLocation() throws Exception {
    if (Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, REGISTRY_KEY, REGISTRY_VALUE)) {
      String railworksPath =
          Advapi32Util.registryGetStringValue(
              WinReg.HKEY_LOCAL_MACHINE,
              REGISTRY_KEY,
              REGISTRY_VALUE);
      if (!railworksPath.trim().isEmpty()) {
        railworksPath += "\\plugins\\" + (System.getProperty("os.arch").contains("64") ? "RailDriver64.dll" : "RailDriver.dll");
        if (new File(railworksPath).exists()) {
          return railworksPath;
        }
      }
    }
    throw new Exception(
        "Registry key for installation path of TrainSimulator not found. Add property native.ddlLocation with absolute path of TrainSimulator installation folder in application.properties.");
  }

  public NativeLibrary getInstance() {
    return instance;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    String ddlLocation;
    if (StringUtils.isNotBlank(configuration.getDllLocation())) {
      ddlLocation = configuration.getDllLocation();
    } else {
      ddlLocation = getDllLocation();
    }
    instance = Native.loadLibrary(ddlLocation, NativeLibrary.class);
    instance.SetRailDriverConnected(true);
    instance.SetRailSimConnected(true);
    logger.info("DLL {} loaded successfully", ddlLocation);
  }
}
