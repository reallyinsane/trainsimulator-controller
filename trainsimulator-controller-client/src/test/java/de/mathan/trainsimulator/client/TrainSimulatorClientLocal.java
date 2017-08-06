package de.mathan.trainsimulator.client;

import java.util.List;

import de.mathan.trainsimulator.client.internal.TrainSimulatorRSClient;
import de.mathan.trainsimulator.model.Controller;
import de.mathan.trainsimulator.model.ControllerValue;
import de.mathan.trainsimulator.model.Locomotive;

public class TrainSimulatorClientLocal{
	
	public static final String SIFA_LIGHT ="VigilLight";
	public static final String SIFA_ALARM="VigilAlarm";
	public static final String SIFA_RESET="VigilReset";

	public static void main(String[] args) throws Exception {
	  Configuration configuration = new Configuration();
	  configuration.setRestHost("localhost");
	  configuration.setRestPort(13913);
		final TrainSimulatorRSClient ts = new TrainSimulatorRSClient(configuration);
		Locomotive locomotive = ts.getLocomotive();
		System.out.println(locomotive.getEngine());
		List<Controller> availableController = locomotive.getController();
		System.out.println("=====");
		for(Controller controller:availableController) {
		  ControllerValue value = ts.getControllerValue(controller);
		  System.out.println(String.format("%s\t%s\t%s\t%s", controller.getValue(), value.getCurrent(), value.getMinimum(), value.getMaximum()));
		}
    System.out.println("=====");
		System.in.read();
	}

}
