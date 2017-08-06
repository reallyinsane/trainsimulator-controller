/*
 * Copyright 2017 Matthias Hanisch (reallyinsane)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mathan.trainsimulator.client.internal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import de.mathan.trainsimulator.TrainSimulatorException;
import de.mathan.trainsimulator.TrainSimulatorService;
import de.mathan.trainsimulator.client.Configuration;
import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.ControlValue;
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

  public ControlValue getControlValue(Control control) {
    return this.client.target(baseUrl()+"control/"+control.getValue()).request(MediaType.APPLICATION_JSON).get(ControlValue.class);
  }
  
  private String baseUrl() {
    return String.format("http://%s:%s/trainsimulator/",
        new Object[] { configuration.getRestHost(), Integer.valueOf(configuration.getRestPort()) });
  }
}
