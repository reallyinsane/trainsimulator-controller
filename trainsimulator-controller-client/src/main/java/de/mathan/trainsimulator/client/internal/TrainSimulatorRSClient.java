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
import de.mathan.trainsimulator.model.Info;


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
  
  public Info getInfo() {
    return this.client.target(baseUrl()+"info").request(MediaType.APPLICATION_JSON).get(Info.class);
  }
  
  public Mapping getMapping(String loco) {
    return this.client.target(baseUrl()+"map").queryParam("loco", loco).request(MediaType.APPLICATION_JSON).get(Mapping.class);
  }
  
  public Control getControl(int controllerId) {
    return this.client.target(baseUrl()+"control/"+controllerId).request(MediaType.APPLICATION_JSON).get(Control.class);
  }

  private String baseUrl() {
    return String.format("http://%s:%s/trainsimulator/",
        new Object[] { this.host, Integer.valueOf(this.port) });
  }
}
