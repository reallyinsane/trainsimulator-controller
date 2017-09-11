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

import java.util.ArrayList;
import java.util.List;

import io.mathan.trainsimulator.TrainSimulatorException;
import io.mathan.trainsimulator.TrainSimulatorService;
import io.mathan.trainsimulator.UnsupportedControlException;
import io.mathan.trainsimulator.model.Control;
import io.mathan.trainsimulator.model.ControlValue;
import io.mathan.trainsimulator.model.Locomotive;
import io.mathan.trainsimulator.model.generic.GenericLocomotive;

public class DefaultTrainSimulatorClient implements TrainSimulatorClient {
  private final TrainSimulatorService client;
  private String currentLocoName = null;
  private List<Control> availableControls = new ArrayList<>();

  public DefaultTrainSimulatorClient(TrainSimulatorService service) {
    this.client = service;
  }

  @Override
  public Locomotive getLocomotive() throws TrainSimulatorException {
    return this.client.getLocomotive();
  }

  @Override
  public GenericLocomotive getGenericLocomotive() throws TrainSimulatorException {
    return this.client.getGenericLocomotive();
  }

  @Override
  public String getLocoName() throws TrainSimulatorException {
    Locomotive locomotive = getLocomotive();
    String locoName = locomotive.getEngine();
    if (locoName != null
        && locoName.length() != 0
        && (this.currentLocoName == null || !locoName.equals(this.currentLocoName))) {
      this.currentLocoName = locoName;
      this.availableControls = locomotive.getControls();
      System.out.println(this.availableControls);
    }
    return locoName;
  }

  @Override
  public boolean is(Control control) throws UnsupportedControlException, TrainSimulatorException {
    if (!has(control)) {
      throw new UnsupportedControlException(control);
    }
    Float value = get(control);
    return Float.valueOf(1.0F).equals(value);
  }

  @Override
  public Float get(Control control) throws UnsupportedControlException, TrainSimulatorException {
    if (!has(control)) {
      throw new UnsupportedControlException(control);
    }
    return get(control, Type.Actual);
  }

  @Override
  public ControlValue getControlValue(Control control)
      throws TrainSimulatorException, UnsupportedControlException {
    return this.client.getControlValue(control);
  }

  @Override
  public Float get(Control control, Type type)
      throws UnsupportedControlException, TrainSimulatorException {
    if (!has(control)) {
      throw new UnsupportedControlException(control);
    }
    ControlValue value = getControlValue(control);
    if (value == null) {
      return null;
    }
    switch (type) {
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

  @Override
  public void press(Control control) throws UnsupportedControlException, TrainSimulatorException {
    if (!has(control)) {
      throw new UnsupportedControlException(control);
    }
    set(control, true);
    set(control, false);
  }

  @Override
  public void set(Control control, boolean value)
      throws UnsupportedControlException, TrainSimulatorException {
    if (!has(control)) {
      throw new UnsupportedControlException(control);
    }
    if (value) {
      set(control, 1.0F);
    } else {
      set(control, 0.0F);
    }
  }

  protected void set(Control control, float value)
      throws UnsupportedControlException, TrainSimulatorException {
    //    Integer id = getIdForControl(control);
    //    if (id == null) {
    //      System.out.println(String.format("WARNING: Cannot set value for control %s, control is not available.", new Object[] { control }));
    //    } else {
    //TODO
    //this.client.control(id.intValue(), value);
    //    }
  }

  @Override
  public boolean has(Control control) {
    return this.availableControls.contains(control);
  }
}
