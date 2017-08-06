package de.mathan.trainsimulator;

import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.ControlValue;
import de.mathan.trainsimulator.model.Locomotive;
import de.mathan.trainsimulator.model.generic.GenericLocomotive;

public interface TrainSimulatorService {
  /**
   * Get the information about the active locomotive and the supported controls.
   * @return
   * @throws TrainSimulatorException
   */
  Locomotive getLocomotive() throws TrainSimulatorException;
  /**
   * Returns the value for the requested control.
   * @param control
   * @return
   * @throws TrainSimulatorException If the request to the server failed.
   * @throws UnsupportedControlException If the control is not supported by the active locomotive.
   */
  ControlValue getControlValue(Control control) throws TrainSimulatorException, UnsupportedControlException;
  /**
   * Returns generic information about the active locomotive.
   * @return
   * @throws TrainSimulatorException
   */
  GenericLocomotive getGenericLocomotive() throws TrainSimulatorException;
  
}
