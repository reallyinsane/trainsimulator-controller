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
package de.mathan.trainsimulator.raspberry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import de.mathan.trainsimulator.TrainSimulatorException;
import de.mathan.trainsimulator.UnsupportedControlException;
import de.mathan.trainsimulator.client.TrainSimulatorClient;
import de.mathan.trainsimulator.client.TrainSimulatorClientFactory;
import de.mathan.trainsimulator.model.Control;

public class GpioClient {
  private static Map<Control, Pin> namePinMapping = new HashMap<Control, Pin>();
  private static String loco;
  private static GpioController gpio = null;

  static {
    namePinMapping.put(Control.Pzb85, RaspiPin.GPIO_00);
    namePinMapping.put(Control.Pzb70, RaspiPin.GPIO_01);
    namePinMapping.put(Control.Pzb55, RaspiPin.GPIO_02);
    namePinMapping.put(Control.SifaLight, RaspiPin.GPIO_03);
    namePinMapping.put(Control.Pzb1000, RaspiPin.GPIO_04);
    namePinMapping.put(Control.Pzb500, RaspiPin.GPIO_05);
    namePinMapping.put(Control.Pzb40, RaspiPin.GPIO_06);
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    TrainSimulatorClient api = TrainSimulatorClientFactory.getInstance();
    final Map<Control, GpioPinDigitalOutput> idOutputMap =
        new HashMap<Control, GpioPinDigitalOutput>();
    checkLocoChanged(api, idOutputMap);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread() {
              @Override
              public void run() {
                if (GpioClient.gpio != null) {
                  for (GpioPinDigitalOutput pin : idOutputMap.values()) {
                    pin.low();
                  }
                  GpioClient.gpio.shutdown();
                }
              }
            });
    int run = 1;
    while (true) {
      for (Control control : idOutputMap.keySet()) {
        GpioPinDigitalOutput pin = idOutputMap.get(control);
        try {
          if (api.is(control)) {
            pin.high();
          } else {
            pin.low();
          }
        } catch (UnsupportedControlException e) {
          pin.low();
        } catch (TrainSimulatorException e) {
          pin.low();
        }
      }
      if (run++ % 100 == 0) {
        checkLocoChanged(api, idOutputMap);
      }
      Thread.sleep(10L);
    }
  }

  private static void checkLocoChanged(
      TrainSimulatorClient api, Map<Control, GpioPinDigitalOutput> idOutputMap) {
    try {
      String newLoco = api.getLocoName();
      if (newLoco != null && !newLoco.equals(loco)) {
        loco = newLoco;
        System.out.println("loco changed to " + loco);
        initGpio(api, idOutputMap);
      }
    } catch (TrainSimulatorException e) {
      System.out.println("failed to connect to server");
    }
  }

  private static void initGpio(
      TrainSimulatorClient api, Map<Control, GpioPinDigitalOutput> idOutputMap) {
    for (GpioPinDigitalOutput pin : idOutputMap.values()) {
      pin.low();
    }
    idOutputMap.clear();
    if (gpio != null) {
      for (GpioPin pin : new ArrayList<GpioPin>(gpio.getProvisionedPins())) {
        gpio.unprovisionPin(new GpioPin[] {pin});
      }
      gpio.shutdown();
    }
    gpio = GpioFactory.getInstance();
    for (Control control : namePinMapping.keySet()) {
      Pin pin = namePinMapping.get(control);
      if (api.has(control)) {
        GpioPinDigitalOutput out = gpio.provisionDigitalOutputPin(pin);
        idOutputMap.put(control, out);
        System.out.println(
            String.format("initialized control %s on %s", new Object[] {control, pin}));
      } else {
        System.out.println(
            String.format(
                "control %s not initialized on %s (no ID for loco)", new Object[] {control, pin}));
      }
    }
  }
}
