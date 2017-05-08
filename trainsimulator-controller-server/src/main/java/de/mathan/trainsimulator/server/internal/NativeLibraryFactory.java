package de.mathan.trainsimulator.server.internal;

import com.sun.jna.Native;

public class NativeLibraryFactory {

	public static NativeLibrary getInstance(String pathToDll) {
		return (NativeLibrary) Native.loadLibrary(pathToDll, NativeLibrary.class);
	}
}
