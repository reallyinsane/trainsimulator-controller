package de.mathan.trainsimulator.server.internal;

import java.io.File;
import java.util.prefs.Preferences;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import de.mathan.trainsimulator.server.TrainSimulatorServer;

public class NativeLibraryFactory {

	public static NativeLibrary getInstance() {
		return (NativeLibrary) Native.loadLibrary(getDllLocation(), NativeLibrary.class);
	}

  public static String getDllLocation() {
    String railworksPath = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\WOW6432Node\\railsimulator.com\\railworks","install_path");
    if(railworksPath!=null&&!railworksPath.trim().isEmpty()) {
      railworksPath+="\\plugins\\RailDriver.dll";
      if(new File(railworksPath).exists()) {
        return railworksPath;
      }
    }
    return Preferences.userNodeForPackage(TrainSimulatorServer.class).get("location", "");
  }

  public static void setDllLocation(String location) {
    Preferences.userNodeForPackage(TrainSimulatorServer.class).put("location", location);
  }
}
