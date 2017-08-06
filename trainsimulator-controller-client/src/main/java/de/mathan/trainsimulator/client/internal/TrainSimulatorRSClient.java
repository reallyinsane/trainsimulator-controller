package de.mathan.trainsimulator.client.internal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import de.mathan.trainsimulator.TrainSimulatorException;
import de.mathan.trainsimulator.TrainSimulatorService;
import de.mathan.trainsimulator.client.Configuration;
import de.mathan.trainsimulator.model.Controller;
import de.mathan.trainsimulator.model.ControllerValue;
import de.mathan.trainsimulator.model.Locomotive;
import de.mathan.trainsimulator.model.generic.GenericLocomotive;


public class TrainSimulatorRSClient implements TrainSimulatorService {

  private final Client client;
  private Configuration configuration;

  public TrainSimulatorRSClient(Configuration configuration) {
    this.configuration = configuration;
    client =ClientBuilder.newClient();
    client.register(JacksonJaxbJsonProvider.class);
  }
  
  public Locomotive getLocomotive() {
    return this.client.target(baseUrl()+"locomotive").request(MediaType.APPLICATION_JSON).get(Locomotive.class);
  }
  
  @Override
  public GenericLocomotive getGenericLocomotive()
      throws TrainSimulatorException {
    return this.client.target(baseUrl()+"generic").request(MediaType.APPLICATION_JSON).get(GenericLocomotive.class);
  }

  public ControllerValue getControllerValue(Controller controller) {
    return this.client.target(baseUrl()+"controller/"+controller.getValue()).request(MediaType.APPLICATION_JSON).get(ControllerValue.class);
  }
  
  private String baseUrl() {
    return String.format("http://%s:%s/trainsimulator/",
        new Object[] { configuration.getRestHost(), Integer.valueOf(configuration.getRestPort()) });
  }
}
