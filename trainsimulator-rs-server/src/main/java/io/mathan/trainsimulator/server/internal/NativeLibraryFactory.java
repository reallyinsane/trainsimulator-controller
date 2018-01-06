/*
 * Copyright 2017 Matthias Hanisch (reallyinsane)
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

package io.mathan.trainsimulator.server.internal;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import io.mathan.trainsimulator.server.TrainSimulatorServer;
import java.io.File;
import java.util.prefs.Preferences;

public class NativeLibraryFactory {

  public static NativeLibrary getInstance() {
    return Native.loadLibrary(getDllLocation(), NativeLibrary.class);
  }

  public static String getDllLocation() {
    String railworksPath =
        Advapi32Util.registryGetStringValue(
            WinReg.HKEY_LOCAL_MACHINE,
            "SOFTWARE\\WOW6432Node\\railsimulator.com\\railworks",
            "install_path");
    if (!railworksPath.trim().isEmpty()) {
      railworksPath += "\\plugins\\RailDriver.dll";
      if (new File(railworksPath).exists()) {
        return railworksPath;
      }
    }
    return Preferences.userNodeForPackage(TrainSimulatorServer.class).get("location", "");
  }

  public static void setDllLocation(String location) {
    Preferences.userNodeForPackage(TrainSimulatorServer.class).put("location", location);
  }
}
