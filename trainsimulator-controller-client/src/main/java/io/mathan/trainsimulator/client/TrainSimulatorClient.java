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

package io.mathan.trainsimulator.client;

import io.mathan.trainsimulator.TrainSimulatorException;
import io.mathan.trainsimulator.TrainSimulatorService;
import io.mathan.trainsimulator.UnsupportedControlException;
import io.mathan.trainsimulator.model.Control;

/**
 * The interface for clients interacting with controls from Train Simulator. As not all locos support all kind of controls the enum {@link Control} is used to identify the control on each request.
 *
 * @author Matthias Hanisch (reallyinsane)
 */
public interface TrainSimulatorClient extends TrainSimulatorService {

  String getLocoName() throws TrainSimulatorException;

  /**
   * Returns whether the current loco supports the given control or not.
   *
   * @param control The control to check.
   * @return <code>True</code> if the control is supported, <code>false</code> otherwise.
   */
  boolean has(Control control);

  /**
   * Returns whether the state of the given control is enabled or not. This should be called for &quot;boolean&quot; controls only. Also check if the control is supported in advance using {@link
   * #has(Control)}.
   *
   * @param control The control to check.
   * @return <code>True</code> if the state of the control is enabled, <code>false</code> otherwise.
   * @throws UnsupportedControlException If the control is not supported. (See {@link #has(Control)})
   */
  boolean is(Control control) throws UnsupportedControlException, TrainSimulatorException;

  Float get(Control control) throws UnsupportedControlException, TrainSimulatorException;

  Float get(Control control, Type type) throws UnsupportedControlException, TrainSimulatorException;

  void press(Control control) throws UnsupportedControlException, TrainSimulatorException;

  void set(Control control, boolean enable)
      throws UnsupportedControlException, TrainSimulatorException;
}
