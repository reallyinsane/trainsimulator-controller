package de.mathan.trainsimulator.client;

import java.util.List;
import java.util.Map;

import de.mathan.trainsimulator.client.internal.TrainSimulatorRSClient;
import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.Info;

public class TrainSimulatorClientLocal{
	
	public static final String SIFA_LIGHT ="VigilLight";
	public static final String SIFA_ALARM="VigilAlarm";
	public static final String SIFA_RESET="VigilReset";

	public static void main(String[] args) throws Exception {
		final TrainSimulatorRSClient ts = new TrainSimulatorRSClient("localhost", 13913);
		Info trainsimulator = ts.getInfo();
		System.out.println(trainsimulator.getLocoName());
		List<Control> controls = trainsimulator.getControls();
		System.out.println("=====");
		for(Control control:controls) {
		  control = ts.getControl(control.getId());
		  System.out.println(String.format("%s\t%s\t%s\t%s\t%s", control.getId(), control.getName(), control.getCurrent(), control.getMinimum(), control.getMaximum()));
		}
    System.out.println("=====");
		System.in.read();
	}

}
