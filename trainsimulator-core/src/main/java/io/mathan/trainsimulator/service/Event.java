/*
 * Copyright 2019 Matthias Hanisch (reallyinsane)
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

import io.mathan.trainsimulator.model.Control;
import io.mathan.trainsimulator.model.ControlData;

public class Event {


  private final Control control;
  private final ControlData controlData;

  public Event(Control control, ControlData controlData) {
    this.control = control;
    this.controlData = controlData;
  }

  public Control getControl() {
    return this.control;
  }

  public ControlData getData() {
    return this.controlData;
  }
}
