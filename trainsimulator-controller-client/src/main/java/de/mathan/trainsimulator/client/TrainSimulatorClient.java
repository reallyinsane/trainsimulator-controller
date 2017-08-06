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
package de.mathan.trainsimulator.client;

import de.mathan.trainsimulator.TrainSimulatorException;
import de.mathan.trainsimulator.TrainSimulatorService;
import de.mathan.trainsimulator.UnsupportedControllerException;
import de.mathan.trainsimulator.model.Controller;

/**
 * The interface for clients interacting with controls from Train Simulator.
 * As not all locos support all kind of controls the enum {@link Controller} is
 * used to identify the control on each request.
 * 
 * @author Matthias Hanisch (reallyinsane)
 */
public interface TrainSimulatorClient extends TrainSimulatorService {
  String getLocoName() throws TrainSimulatorException;

  /**
   * Returns whether the current loco supports the given control or not.
   * @param controller The control to check.
   * @return <code>True</code> if the control is supported, <code>false</code> otherwise.
   */
  boolean has(Controller controller);

  /**
   * Returns whether the state of the given control is enabled or not. This
   * should be called for &quot;boolean&quot; controls only. Also check if the control
   * is supported in advance using {@link #has(Controller )}.
   * @param controller The control to check.
   * @return <code>True</code> if the state of the control is enabled, <code>false</code> otherwise.
   * @throws UnsupportedControllerException If the control is not supported. (See {@link #has(Controller)})  
   */
  boolean is(Controller controller) throws UnsupportedControllerException, TrainSimulatorException;

  Float get(Controller controller) throws UnsupportedControllerException, TrainSimulatorException;

  Float get(Controller controller, Type type) throws UnsupportedControllerException, TrainSimulatorException;

  void press(Controller controller) throws UnsupportedControllerException, TrainSimulatorException;

  void set(Controller controller, boolean enable) throws UnsupportedControllerException, TrainSimulatorException;
}
