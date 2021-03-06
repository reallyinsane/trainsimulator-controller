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

package io.mathan.trainsimulator.raspberry;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import io.mathan.trainsimulator.model.Control;
import io.mathan.trainsimulator.model.ControlData;
import io.mathan.trainsimulator.model.Locomotive;
import io.mathan.trainsimulator.service.Connector;
import io.mathan.trainsimulator.service.Event;
import io.mathan.trainsimulator.service.LocoUpdate;
import io.mathan.trainsimulator.service.Present;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class GpioClient implements InitializingBean {

  private Logger logger = LoggerFactory.getLogger(GpioClient.class);


  private Map<String, List<Pin>> namePinMapping = new HashMap<>();
  private Map<String, List<GpioPinDigitalOutput>> idOutputMap = new HashMap<>();
  private Locomotive locomotive;
  private String loco;
  private GpioController gpio = null;

  private Connector connector;

  {
    //TODO: move to config
    namePinMapping.put(Control.Pzb85, list(RaspiPin.GPIO_00));
    namePinMapping.put(Control.Pzb70, list(RaspiPin.GPIO_01));
    namePinMapping.put(Control.Pzb55, list(RaspiPin.GPIO_02));
    namePinMapping.put(Control.SifaLight, list(RaspiPin.GPIO_03));
    namePinMapping.put(Control.SifaAlarm, list(RaspiPin.GPIO_25, RaspiPin.GPIO_27));
    namePinMapping.put(Control.Pzb1000, list(RaspiPin.GPIO_04));
    namePinMapping.put(Control.Pzb500, list(RaspiPin.GPIO_05));
    namePinMapping.put(Control.Pzb40, list(RaspiPin.GPIO_06));
  }

  public GpioClient(Connector connector) {
    this.connector = connector;
  }

  private static List<Pin> list(Pin pin, Pin... further) {
    List<Pin> pins = new ArrayList<>();
    pins.add(pin);
    if (further != null) {
      pins.addAll(Arrays.asList(further));
    }
    return pins;
  }

  @Present
  public void present(Event event) {
    logger.info("present {}", event.getControl());
    Optional<List<GpioPinDigitalOutput>> pins = Optional.ofNullable(idOutputMap.get(event.getControl()));
    if (pins.isPresent()) {
      for (GpioPinDigitalOutput pin : pins.get()) {
        if (Float.valueOf(1.0F).equals(event.getData().getCurrent())) {
          pin.high();
        } else {
          pin.low();
        }
      }
    }
  }

  @LocoUpdate
  public void updateLoco(Locomotive locomotive) {
    this.locomotive = locomotive;
    String newLoco = getLocoName(locomotive);
    if (newLoco != null && !newLoco.equals(loco)) {
      loco = newLoco;
      logger.info("loco changed to {}", loco);
      initGpio();
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    updateLoco(connector.getLocomotive());
    logger.info("startup test started");
    String[] controls = {Control.Pzb55, Control.Pzb70, Control.Pzb85, Control.Pzb40, Control.Pzb500, Control.Pzb1000, Control.SifaLight, Control.SifaAlarm};
    for (String control : controls) {
      logger.info(String.format("%s ON", control));

      present(getOnEvent(control));
      Thread.sleep(500);
      logger.info(String.format("%s OFF", control));
      present(getOffEvent(control));
      Thread.sleep(500);
    }
    logger.info("startup test finished");
  }

  private String getLocoName(Locomotive locomotive) {
    return locomotive.getEngine();
  }

  private Event getOnEvent(String control) {
    return getEvent(control, 1.0f);
  }

  private Event getOffEvent(String control) {
    return getEvent(control, 0.0f);
  }

  private Event getEvent(String control, float value) {
    ControlData data = new ControlData();
    data.setCurrent(value);
    return new Event(control, data);
  }

  private void initGpio() {
    idOutputMap.values().forEach(l -> l.forEach(pin -> pin.low()));
    idOutputMap.clear();
    if (gpio != null) {
      for (GpioPin pin : new ArrayList<GpioPin>(gpio.getProvisionedPins())) {
        gpio.unprovisionPin(new GpioPin[]{pin});
      }
      gpio.shutdown();
    }
    gpio = GpioFactory.getInstance();
    for (String control : namePinMapping.keySet()) {
      List<Pin> pins = namePinMapping.get(control);
      List<GpioPinDigitalOutput> outs = new ArrayList<>();
      for (Pin pin : pins) {
        GpioPinDigitalOutput out = gpio.provisionDigitalOutputPin(pin);
        outs.add(out);
        if (locomotive.getControls().contains(control)) {
          logger.info("initialized control {} on {}", control, pin);
        } else {
          logger.info("control {} not available for loco", control);
        }
      }
      if (!outs.isEmpty()) {
        idOutputMap.put(control, outs);
      }
    }
  }
}
