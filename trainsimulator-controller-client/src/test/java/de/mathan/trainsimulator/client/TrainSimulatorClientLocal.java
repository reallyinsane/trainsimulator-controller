package de.mathan.trainsimulator.client;

import java.util.Map;

import de.mathan.trainsimulator.client.internal.TrainSimulatorRSClient;

public class TrainSimulatorClientLocal{
	
	public static final String SIFA_LIGHT ="VigilLight";
	public static final String SIFA_ALARM="VigilAlarm";
	public static final String SIFA_RESET="VigilReset";

	public static void main(String[] args) throws Exception {
		final TrainSimulatorRSClient ts = new TrainSimulatorRSClient("localhost", 13913);
		Map<String, Integer> controller = ts.getControllerList();
		int index=0;
		System.out.println("=====");
		for(String c:controller.keySet()) {
		  System.out.println((index++)+"\t"+c+"\t"+ts.getControllerValue(controller.get(c), 0)+"\t"+ts.getControllerValue(controller.get(c), 1)+"\t"+ts.getControllerValue(controller.get(c), 2));
		}
    System.out.println("=====");
		System.in.read();
		ts.setControllerValue(22, 1f);
    System.out.println("=====");
    for(String c:controller.keySet()) {
      System.out.println(c+"\t"+ts.getControllerValue(controller.get(c), 0));
    }
    System.out.println("=====");
		
	}

}
