package de.mathan.trainsimulator.client.internal;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import de.mathan.trainsimulator.TrainSimulatorService;
import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.Mapping;
import de.mathan.trainsimulator.model.TrainSimulator;


public class TrainSimulatorRSClient implements TrainSimulatorService {

  private final String host;
  private final int port;
  private final Client client;

  public TrainSimulatorRSClient(String host, int port)
  {
    this.host = host;
    this.port = port;
    client =ClientBuilder.newClient();
    client.register(JacksonJaxbJsonProvider.class);
  }
  
  public TrainSimulator getInfo() {
    return this.client.target(baseUrl()+"info").request(MediaType.APPLICATION_JSON).get(TrainSimulator.class);
  }
  
  public Mapping getMapping(String loco) {
    return this.client.target(baseUrl()+"map").queryParam("loco", loco).request(MediaType.APPLICATION_JSON).get(Mapping.class);
  }
  
  public Control getControl(int controllerId) {
    return this.client.target(baseUrl()+"control/"+controllerId).request(MediaType.APPLICATION_JSON).get(Control.class);
  }

  @Deprecated
  public String getLocoName() {
    TrainSimulator trainSimulator = this.client.target(baseUrl()+"info").request(MediaType.APPLICATION_JSON).get(TrainSimulator.class);
    return trainSimulator.getLocoName();
    
//    WebResource resource = this.client.resource(baseUrl() + "loconame");
//    return (String) resource.get(String.class);
  }

  @Deprecated
  public boolean isCombinedThrottleBrake() {
//    WebResource resource = this.client
//        .resource(baseUrl() + "combinedThrottleBrake");
//    return Boolean.valueOf((String) resource.get(String.class)).booleanValue();
    return false;
  }

  @Deprecated
  public Map<String, Integer> getControllerList() {
//    WebResource resource = this.client.resource(baseUrl() + "list");
//    String result = (String) resource.get(String.class);
//    StringTokenizer tokenizer = new StringTokenizer(result, "::");
//    int index = 0;
//    Map<String, Integer> map = new HashMap<String,Integer>();
//    while (tokenizer.hasMoreTokens()) {
//      String token = tokenizer.nextToken();
//      map.put(token, Integer.valueOf(index++));
//    }
//    return map;
    return Collections.emptyMap();
  }

//  public Map<String, String> getMapping(String loco) {
//    WebResource resource = this.client.resource(baseUrl() + "mapping");
//    String result = (String) resource.queryParam("loco", loco)
//        .get(String.class);
//    StringTokenizer tokenizer = new StringTokenizer(result, ";");
//    Map<String, String> map = new HashMap<String, String>();
//    while (tokenizer.hasMoreTokens()) {
//      String token = tokenizer.nextToken();
//      int index = token.indexOf('=');
//      map.put(token.substring(0, index), token.substring(index + 1));
//    }
//    return map;
//    return Collections.emptyMap();
//  }

  @Deprecated
  public float getControllerValue(int id, int type) {
//    WebResource resource = this.client.resource(baseUrl() + "controller/" + id);
//    return Float.valueOf((String) resource
//        .queryParam("type", String.valueOf(type)).get(String.class))
//        .floatValue();
    return 0f;
  }

  @Deprecated
  public void setControllerValue(int id, float value) {
//    WebResource resource = this.client.resource(baseUrl() + "controller/" + id);
//    resource.queryParam("value", String.valueOf(value))
//        .put(ClientResponse.class);
  }

  private String baseUrl() {
    return String.format("http://%s:%s/trainsimulator/",
        new Object[] { this.host, Integer.valueOf(this.port) });
  }
}
