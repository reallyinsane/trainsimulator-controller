package de.mathan.trainsimulator.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.mathan.trainsimulator.client.TrainSimulator;
import de.mathan.trainsimulator.client.TrainSimulatorFactory;

public class TrainSimulatorClientLocal implements TrainSimulator {

	private final String host;
	private final int port;

	public TrainSimulatorClientLocal(String host, int port) {
		this.host = host;
		this.port = port;

	}

	public String getLocoName() {
		Client client = Client.create();
		try {
			WebResource resource = client.resource(baseUrl() + "loconame");
			return resource.get(String.class);
		} finally {
			client.destroy();
		}
	}

	public boolean isCombinedThrottleBrake() {
		Client client = Client.create();
		try {
			WebResource resource = client.resource(baseUrl() + "combinedThrottleBrake");
			return Boolean.valueOf(resource.get(String.class)).booleanValue();
		} finally {
			client.destroy();
		}
	}

	public Map<String, Integer> getControllerList() {
		Client client = Client.create();
		try {
			WebResource resource = client.resource(baseUrl() + "list");
			String result = resource.get(String.class);
			StringTokenizer tokenizer = new StringTokenizer(result, "::");
			int index = 0;
			Map<String, Integer> map = new HashMap<String, Integer>();
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				map.put(token, Integer.valueOf(index++));
			}
			return map;
		} finally {
			client.destroy();
		}
	}

	public float getControllerValue(int id, int type) {
		Client client = Client.create();
		try {
			WebResource resource = client.resource(baseUrl() + "controller/" + id);
			return Float.valueOf(resource.queryParam("type", String.valueOf(type)).get(String.class)).floatValue();
		} finally {
			client.destroy();
		}
	}

	public void setControllerValue(int id, float value) {
		Client client = Client.create();
		try {
			WebResource resource = client.resource(baseUrl() + "controller/" + id);
			resource.queryParam("value", String.valueOf(value)).put(ClientResponse.class);
		} finally {
			client.destroy();
		}
	}

	private String baseUrl() {
		return String.format("http://%s:%s/trainsimulator/", host, port);
	}
	
	public static final String SIFA_LIGHT ="VigilLight";
	public static final String SIFA_ALARM="VigilAlarm";
	public static final String SIFA_RESET="VigilReset";

	public static void main(String[] args) throws InterruptedException {
		final TrainSimulator ts = TrainSimulatorFactory.getInstance("localhost", 13913);
		Map<String, Integer> controller = ts.getControllerList();
    System.out.println(controller);
    int sifaLight = controller.get(SIFA_LIGHT);
    int sifaAlarm = controller.get(SIFA_ALARM);
    final int sifaReset = controller.get(SIFA_RESET);
    try {
      System.in.read();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    ts.setControllerValue(sifaReset, 1);
    ts.setControllerValue(sifaReset, 0);
	}

}
