package de.mathan.trainsimulator.server.internal;

import com.sun.jna.Library;

public interface NativeLibrary extends Library {
	String GetControllerList();

	String GetLocoName();

	float GetControllerValue(int id, int type);

	void SetControllerValue(int id, float value);

	float GetRailSimValue(int id, float type);

	void SetRailSimValue(int id, float value);

	void SetRailSimConnected(boolean connect);

	void SetRailDriverConnected(boolean connect);

	boolean GetRailSimLocoChanged();

	boolean GetRailSimCombinedThrottleBrake();
}