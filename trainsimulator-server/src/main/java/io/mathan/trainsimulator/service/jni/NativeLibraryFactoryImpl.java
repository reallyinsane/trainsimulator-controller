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
import io.mathan.trainsimulator.service.Service;
import java.io.File;
import java.util.prefs.Preferences;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Default implementation for production mode loading {@link NativeLibrary} via JNI and accessing the Windows DLL.
 */
@Component
@Profile("native")
public class NativeLibraryFactoryImpl implements NativeLibraryFactory, InitializingBean {

  private static NativeLibrary instance = null;
  private final NativeConfiguration configuration;

  public NativeLibraryFactoryImpl(NativeConfiguration configuration) {

    this.configuration = configuration;
  }

  public static String getDllLocation() {
    String railworksPath =
        Advapi32Util.registryGetStringValue(
            WinReg.HKEY_LOCAL_MACHINE,
            "SOFTWARE\\WOW6432Node\\railsimulator.com\\railworks",
            "install_path");
    if (!railworksPath.trim().isEmpty()) {
      railworksPath += "\\plugins\\" + (System.getProperty("os.arch").contains("64") ? "RailDriver64.dll" : "RailDriver.dll");
      if (new File(railworksPath).exists()) {
        return railworksPath;
      }
    }
    return Preferences.userNodeForPackage(Service.class).get("location", "");
  }

  public static void setDllLocation(String location) {
    Preferences.userNodeForPackage(Service.class).put("location", location);
  }

  public NativeLibrary getInstance() {
    return instance;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (StringUtils.isNotBlank(configuration.getDllLocation())) {
      instance = Native.loadLibrary(configuration.getDllLocation(), NativeLibrary.class);
    } else {
      instance = Native.loadLibrary(getDllLocation(), NativeLibrary.class);
    }
    instance.SetRailDriverConnected(true);
    instance.SetRailSimConnected(true);

  }
}
