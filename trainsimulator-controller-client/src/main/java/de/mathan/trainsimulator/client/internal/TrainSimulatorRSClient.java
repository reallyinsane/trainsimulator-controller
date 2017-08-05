package de.mathan.trainsimulator.client.internal;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import de.mathan.trainsimulator.TrainSimulatorService;
import de.mathan.trainsimulator.client.Configuration;
import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.Mapping;
import de.mathan.trainsimulator.model.Info;


public class TrainSimulatorRSClient implements TrainSimulatorService {

  private final Client client;
  private Configuration configuration;

  public TrainSimulatorRSClient(Configuration configuration) {
    this.configuration = configuration;
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
        new Object[] { configuration.getRestHost(), Integer.valueOf(configuration.getRestPort()) });
  }
}
