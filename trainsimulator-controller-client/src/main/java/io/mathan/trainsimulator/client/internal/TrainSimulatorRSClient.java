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
package io.mathan.trainsimulator.client.internal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import io.mathan.trainsimulator.TrainSimulatorException;
import io.mathan.trainsimulator.TrainSimulatorService;
import io.mathan.trainsimulator.client.Configuration;
import io.mathan.trainsimulator.model.Control;
import io.mathan.trainsimulator.model.ControlValue;
import io.mathan.trainsimulator.model.Locomotive;
import io.mathan.trainsimulator.model.generic.GenericLocomotive;

public class TrainSimulatorRSClient implements TrainSimulatorService {

  private final Client client;
  private final Configuration configuration;

  public TrainSimulatorRSClient(Configuration configuration) {
    this.configuration = configuration;
    this.client = ClientBuilder.newClient();
    this.client.register(JacksonJaxbJsonProvider.class);
  }

  @Override
  public Locomotive getLocomotive() {
    return this.client
        .target(baseUrl() + "locomotive")
        .request(MediaType.APPLICATION_JSON)
        .get(Locomotive.class);
  }

  @Override
  public GenericLocomotive getGenericLocomotive() throws TrainSimulatorException {
    return this.client
        .target(baseUrl() + "generic")
        .request(MediaType.APPLICATION_JSON)
        .get(GenericLocomotive.class);
  }

  @Override
  public ControlValue getControlValue(Control control) {
    return this.client
        .target(baseUrl() + "control/" + control.getValue())
        .request(MediaType.APPLICATION_JSON)
        .get(ControlValue.class);
  }

  private String baseUrl() {
    return String.format(
        "http://%s:%s/trainsimulator/",
        new Object[] {
          this.configuration.getRestHost(), Integer.valueOf(this.configuration.getRestPort())
        });
  }
}