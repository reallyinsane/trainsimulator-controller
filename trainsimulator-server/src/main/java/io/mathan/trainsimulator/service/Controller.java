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
package io.mathan.trainsimulator.service;

import io.mathan.trainsimulator.model.ControlData;
import io.mathan.trainsimulator.model.Locomotive;
import io.mathan.trainsimulator.model.generic.GenericLocomotive;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trainsimulator")
public class Controller {

  private Service service;

  private Map<String, ControlData> currentData = new HashMap<>();

  public Controller(Service service) {
    this.service = service;
  }

  @GetMapping(value = "/locomotive", produces = {MediaType.APPLICATION_JSON_VALUE})
  public Locomotive getLocomotive() throws TrainSimulatorException {
    return service.getLocomotive();
  }

  @GetMapping(value = "/generic", produces = {MediaType.APPLICATION_JSON_VALUE})
  public GenericLocomotive getGenericLocomotive() throws TrainSimulatorException {
    return service.getGenericLocomotive();
  }

  @GetMapping(value = "/control/{control}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public ControlData getControlValue(@PathVariable("control") String control) throws TrainSimulatorException, UnsupportedControlException {
    return currentData.get(control);
  }

  @PutMapping("/control/{control}")
  public void setControlValue(@PathVariable("control") String control, @RequestParam(name = "value") Float value) throws TrainSimulatorException, UnsupportedControlException {
    ControlData data = new ControlData();
    data.setCurrent(value);
    Event event = new Event(control, data);
    service.raiseEvent(event);
  }

  @Present
  public void present(Event event) {
    this.currentData.put(event.getControl(), event.getData());
  }

}
