package de.mathan.trainsimulator.client;

import java.util.ArrayList;
import java.util.List;

import de.mathan.trainsimulator.TrainSimulatorException;
import de.mathan.trainsimulator.TrainSimulatorService;
import de.mathan.trainsimulator.UnsupportedControllerException;
import de.mathan.trainsimulator.model.Controller;
import de.mathan.trainsimulator.model.ControllerValue;
import de.mathan.trainsimulator.model.Locomotive;
import de.mathan.trainsimulator.model.generic.GenericLocomotive;

public class DefaultTrainSimulatorClient
  implements TrainSimulatorClient
{
  private final TrainSimulatorService client;
  private String currentLocoName = null;
  private List<Controller> availableController = new ArrayList<>();
  
  public DefaultTrainSimulatorClient(TrainSimulatorService service) {
    this.client = service;
  }
  
  @Override
  public Locomotive getLocomotive() throws TrainSimulatorException {
    return this.client.getLocomotive();
  }
  
  @Override
  public GenericLocomotive getGenericLocomotive()
      throws TrainSimulatorException {
    return this.client.getGenericLocomotive();
  }
  
  public String getLocoName() throws TrainSimulatorException {
    Locomotive locomotive = getLocomotive();
    String locoName = locomotive.getEngine();
    if ((locoName != null) && (locoName.length() != 0) && (
      (this.currentLocoName == null) || (!locoName.equals(this.currentLocoName))))
    {
      this.currentLocoName = locoName;
      availableController = locomotive.getController();
      System.out.println(this.availableController);
    }
    return locoName;
  }
  
  public boolean is(Controller controller) throws UnsupportedControllerException, TrainSimulatorException {
    if(!has(controller)) {
      throw new UnsupportedControllerException(controller);
    }
    Float value = get(controller);
    return Float.valueOf(1.0F).equals(value);
  }
  
  public Float get(Controller controller) throws UnsupportedControllerException, TrainSimulatorException {
    if(!has(controller)) {
      throw new UnsupportedControllerException(controller);
    }
    return get(controller, Type.Actual);
  }
  
  @Override
  public ControllerValue getControllerValue(Controller controller)
      throws TrainSimulatorException, UnsupportedControllerException {
    return this.client.getControllerValue(controller);
  }
  
  public Float get(Controller controller, Type type) throws UnsupportedControllerException, TrainSimulatorException {
    if(!has(controller)) {
      throw new UnsupportedControllerException(controller);
    }
    ControllerValue value = getControllerValue(controller);
    if(value==null) {
      return null;
    }
    switch(type) {
    case Actual:
      return value.getCurrent();
    case Minimum:
      return value.getMinimum();
    case Maximum:
      return value.getMaximum();
    default:
      throw new IllegalArgumentException();
    }
  }
  
  public void press(Controller controller) throws UnsupportedControllerException, TrainSimulatorException {
    if(!has(controller)) {
      throw new UnsupportedControllerException(controller);
    }
    set(controller, true);
    set(controller, false);
  }
  
  public void set(Controller controller, boolean value) throws UnsupportedControllerException, TrainSimulatorException {
    if(!has(controller)) {
      throw new UnsupportedControllerException(controller);
    }
    if (value) {
      set(controller, 1.0F);
    } else {
      set(controller, 0.0F);
    }
  }
  
  protected void set(Controller controller, float value) throws UnsupportedControllerException, TrainSimulatorException {
//    Integer id = getIdForControl(controller);
//    if (id == null) {
//      System.out.println(String.format("WARNING: Cannot set value for control %s, control is not available.", new Object[] { controller }));
//    } else {
      //TODO
      //this.client.setControllerValue(id.intValue(), value);
//    }
  }
  
  public boolean has(Controller controller) {
    return availableController.contains(controller);
  }
}
