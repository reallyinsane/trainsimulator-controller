package de.mathan.trainsimulator;

import de.mathan.trainsimulator.model.Controller;
import de.mathan.trainsimulator.model.ControllerValue;
import de.mathan.trainsimulator.model.Locomotive;
import de.mathan.trainsimulator.model.generic.GenericLocomotive;

public interface TrainSimulatorService {
  /**
   * Get the information about the active locomotive and the supported controller.
   * @return
   * @throws TrainSimulatorException
   */
  Locomotive getLocomotive() throws TrainSimulatorException;
  /**
   * Returns the value for the requested controller.
   * @param controller
   * @return
   * @throws TrainSimulatorException If the request to the server failed.
   * @throws UnsupportedControllerException If the controller is not supported by the active locomotive.
   */
  ControllerValue getControllerValue(Controller controller) throws TrainSimulatorException, UnsupportedControllerException;
  /**
   * Returns generic information about the active locomotive.
   * @return
   * @throws TrainSimulatorException
   */
  GenericLocomotive getGenericLocomotive() throws TrainSimulatorException;
  
}
