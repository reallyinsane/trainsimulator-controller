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
