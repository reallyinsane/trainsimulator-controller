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

import java.util.List;

import de.mathan.trainsimulator.client.internal.TrainSimulatorRSClient;
import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.ControlValue;
import de.mathan.trainsimulator.model.Locomotive;

public class TrainSimulatorClientLocal {

  public static final String SIFA_LIGHT = "VigilLight";
  public static final String SIFA_ALARM = "VigilAlarm";
  public static final String SIFA_RESET = "VigilReset";

  public static void main(String[] args) throws Exception {
    Configuration configuration = new Configuration();
    configuration.setRestHost("localhost");
    configuration.setRestPort(13913);
    final TrainSimulatorRSClient ts = new TrainSimulatorRSClient(configuration);
    Locomotive locomotive = ts.getLocomotive();
    System.out.println(locomotive.getEngine());
    List<Control> availableControls = locomotive.getControls();
    System.out.println("=====");
    for (Control control : availableControls) {
      ControlValue value = ts.getControlValue(control);
      System.out.println(
          String.format(
              "%s\t%s\t%s\t%s",
              control.getValue(), value.getCurrent(), value.getMinimum(), value.getMaximum()));
    }
    System.out.println("=====");
    System.in.read();
  }
}
