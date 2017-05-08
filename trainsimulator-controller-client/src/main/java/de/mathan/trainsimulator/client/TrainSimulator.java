package de.mathan.trainsimulator.client;

import java.util.Map;

public interface TrainSimulator {
	String getLocoName();

	boolean isCombinedThrottleBrake();

	Map<String, Integer> getControllerList();

	float getControllerValue(int id, int type);

	void setControllerValue(int id, float value);
}
