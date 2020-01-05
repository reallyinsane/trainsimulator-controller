package io.mathan.trainsimulator.service;

import io.mathan.trainsimulator.model.Control;
import io.mathan.trainsimulator.model.ControlData;
import io.mathan.trainsimulator.model.Locomotive;
import io.mathan.trainsimulator.model.generic.GenericLocomotive;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trainsimulator")
public class Controller {

  private Service service;

  private Map<Control, ControlData> currentData = new HashMap<>();

  public Controller(Service service) {
    this.service = service;
  }

  @GetMapping("/locomotive")
  public Locomotive getLocomotive() throws TrainSimulatorException {
    return service.getLocomotive();
  }

  @GetMapping("/generic")
  public GenericLocomotive getGenericLocomotive() throws TrainSimulatorException {
    return service.getGenericLocomotive();
  }

  @GetMapping("/control/{control}")
  public ControlData getControlValue(@PathVariable("control") Control control) throws TrainSimulatorException, UnsupportedControlException {
    return currentData.get(control);
  }

  @Present
  public void present(Event event) {
    this.currentData.put(event.getControl(), event.getValue());
  }

}
