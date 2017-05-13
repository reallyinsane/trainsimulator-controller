package de.mathan.trainsimulator.client.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.mathan.trainsimulator.client.TrainSimulator;

public class TrainSimulatorRSClient {

  private final String host;
  private final int port;
  private final Client client;

  public TrainSimulatorRSClient(String host, int port)
  {
    this.host = host;
    this.port = port;
    this.client = Client.create();
  }

  public String getLocoName() {
    WebResource resource = this.client.resource(baseUrl() + "loconame");
    return (String) resource.get(String.class);
  }

  public boolean isCombinedThrottleBrake() {
    WebResource resource = this.client
        .resource(baseUrl() + "combinedThrottleBrake");
    return Boolean.valueOf((String) resource.get(String.class)).booleanValue();
  }

  public Map<String, Integer> getControllerList() {
    WebResource resource = this.client.resource(baseUrl() + "list");
    String result = (String) resource.get(String.class);
    StringTokenizer tokenizer = new StringTokenizer(result, "::");
    int index = 0;
    Map<String, Integer> map = new HashMap();
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      map.put(token, Integer.valueOf(index++));
    }
    return map;
  }

  public Map<String, String> getMapping(String loco) {
    WebResource resource = this.client.resource(baseUrl() + "mapping");
    String result = (String) resource.queryParam("loco", loco)
        .get(String.class);
    StringTokenizer tokenizer = new StringTokenizer(result, ";");
    Map<String, String> map = new HashMap();
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      int index = token.indexOf('=');
      map.put(token.substring(0, index), token.substring(index + 1));
    }
    return map;
  }

  public float getControllerValue(int id, int type) {
    WebResource resource = this.client.resource(baseUrl() + "controller/" + id);
    return Float.valueOf((String) resource
        .queryParam("type", String.valueOf(type)).get(String.class))
        .floatValue();
  }

  public void setControllerValue(int id, float value) {
    WebResource resource = this.client.resource(baseUrl() + "controller/" + id);
    resource.queryParam("value", String.valueOf(value))
        .put(ClientResponse.class);
  }

  private String baseUrl() {
    return String.format("http://%s:%s/trainsimulator/",
        new Object[] { this.host, Integer.valueOf(this.port) });
  }
}
