/*
 * Copyright 2020 Matthias Hanisch (reallyinsane)
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
package io.mathan.trainsimulator.client;

import io.mathan.trainsimulator.model.ControlData;
import io.mathan.trainsimulator.model.Locomotive;
import io.mathan.trainsimulator.model.generic.GenericLocomotive;
import io.mathan.trainsimulator.service.Connector;
import io.mathan.trainsimulator.service.TrainSimulatorException;
import io.mathan.trainsimulator.service.UnsupportedControlException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("client")
public class RestConnector implements Connector, InitializingBean {

  private TrainsimulatorConfiguration configuration;

  private String url;
  private RestTemplate template = new RestTemplate();

  public RestConnector(TrainsimulatorConfiguration configuration) {
    this.configuration = configuration;
    this.url = String.format("http://%s:%s/trainsimulator/", configuration.getHost(), configuration.getPort());
  }

  @Override
  public Locomotive getLocomotive() throws TrainSimulatorException {
    ResponseEntity<Locomotive> response = template.getForEntity(url + "locomotive", Locomotive.class);
    if (HttpStatus.OK.equals(response.getStatusCode())) {
      return response.getBody();
    }
    throw new TrainSimulatorException(String.format("Could not access server, http response=%s", response.getStatusCode()));
  }

  @Override
  public ControlData getControlData(String control) throws TrainSimulatorException, UnsupportedControlException {
    ResponseEntity<ControlData> response = template.getForEntity(url + "control/" + control, ControlData.class);
    if (HttpStatus.OK.equals(response.getStatusCode())) {
      return response.getBody();
    }
    throw new TrainSimulatorException(String.format("Could not access server, http response=%s", response.getStatusCode()));
  }

  @Override
  public void setControlData(String control, ControlData data) throws TrainSimulatorException, UnsupportedControlException {
    //TODO: implement
  }

  @Override
  public GenericLocomotive getGenericLocomotive() throws TrainSimulatorException {
    ResponseEntity<GenericLocomotive> response = template.getForEntity(url + "generic", GenericLocomotive.class);
    if (HttpStatus.OK.equals(response.getStatusCode())) {
      return response.getBody();
    }
    throw new TrainSimulatorException(String.format("Could not access server, http response=%s", response.getStatusCode()));
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    getGenericLocomotive();
  }
}
