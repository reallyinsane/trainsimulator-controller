package de.mathan.trainsimulator.client.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.mathan.trainsimulator.client.TrainSimulator;

public class TrainSimulatorClient implements TrainSimulator {

  private final String host;
  private final int port;
  private final Client client;

  public TrainSimulatorClient(String host, int port) {
    this.host = host;
    this.port = port;
    client = Client.create();
  }

  public String getLocoName() {
    WebResource resource = client.resource(baseUrl() + "loconame");
    return resource.get(String.class);
  }

  public boolean isCombinedThrottleBrake() {
    WebResource resource = client.resource(baseUrl() + "combinedThrottleBrake");
    return Boolean.valueOf(resource.get(String.class)).booleanValue();
  }

  public Map<String, Integer> getControllerList() {
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
  }

  public float getControllerValue(int id, int type) {
    WebResource resource = client.resource(baseUrl() + "controller/" + id);
    return Float.valueOf(resource.queryParam("type", String.valueOf(type)).get(String.class)).floatValue();
  }

  public void setControllerValue(int id, float value) {
    WebResource resource = client.resource(baseUrl() + "controller/" + id);
    resource.queryParam("value", String.valueOf(value)).put(ClientResponse.class);
  }

  private String baseUrl() {
    return String.format("http://%s:%s/trainsimulator/", host, port);
  }

}
