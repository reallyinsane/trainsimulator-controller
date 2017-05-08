package de.mathan.trainsimulator.client;

import de.mathan.trainsimulator.client.internal.TrainSimulatorClient;

public class TrainSimulatorFactory {

	public static TrainSimulator getInstance(String host, int port) {
		return new TrainSimulatorClient(host, port);
	}
}
