package de.mathan.trainsimulator.client;

import java.util.Map;

public class TrainSimulatorClientLocal{
	
	public static final String SIFA_LIGHT ="VigilLight";
	public static final String SIFA_ALARM="VigilAlarm";
	public static final String SIFA_RESET="VigilReset";

	public static void main(String[] args) throws Exception {
		final TrainSimulator ts = TrainSimulatorFactory.getInstance("localhost", 13913);
		Map<String, Integer> controller = ts.getControllerList();
		System.out.println("=====");
		for(String c:controller.keySet()) {
		  System.out.println(c+"\t"+ts.getControllerValue(controller.get(c), 0));
		}
    System.out.println("=====");
		System.in.read();
    System.out.println("=====");
    for(String c:controller.keySet()) {
      System.out.println(c+"\t"+ts.getControllerValue(controller.get(c), 0));
    }
    System.out.println("=====");
		
	}

}
