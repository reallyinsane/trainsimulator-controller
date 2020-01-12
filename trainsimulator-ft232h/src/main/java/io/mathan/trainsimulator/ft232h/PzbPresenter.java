/*
 * Copyright 2020 Matthias Hanisch (reallyinsane)
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
package io.mathan.trainsimulator.ft232h;

import io.mathan.trainsimulator.model.Control;
import io.mathan.trainsimulator.model.ControlData;
import io.mathan.trainsimulator.service.Event;
import io.mathan.trainsimulator.service.Present;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Component consuming {@link Event} and delegating them to the pins on FT232h.
 */
@Component
@Profile("pzb")
public class PzbPresenter implements InitializingBean {

  private Logger logger = LoggerFactory.getLogger(PzbPresenter.class);

  private Map<Control, Pin> map = new HashMap<>();

  private PzbConfiguration configuration;
  private Ft232h ft232h;

  public PzbPresenter(PzbConfiguration configuration) {
    this.configuration = configuration;
    if(StringUtils.isNotBlank(configuration.getPzb55())) {
      map.put(Control.Pzb55, Pin.valueOf(configuration.getPzb55()));
    }
    if(StringUtils.isNotBlank(configuration.getPzb70())) {
      map.put(Control.Pzb70, Pin.valueOf(configuration.getPzb70()));
    }
    if(StringUtils.isNotBlank(configuration.getPzb85())) {
      map.put(Control.Pzb85, Pin.valueOf(configuration.getPzb85()));
    }
    if(StringUtils.isNotBlank(configuration.getPzb40())) {
      map.put(Control.Pzb40, Pin.valueOf(configuration.getPzb40()));
    }
    if(StringUtils.isNotBlank(configuration.getPzb500())) {
      map.put(Control.Pzb500, Pin.valueOf(configuration.getPzb500()));
    }
    if(StringUtils.isNotBlank(configuration.getPzb1000())) {
      map.put(Control.Pzb1000, Pin.valueOf(configuration.getPzb1000()));
    }
    if(StringUtils.isNotBlank(configuration.getSifaLight())) {
      map.put(Control.SifaLight, Pin.valueOf(configuration.getSifaLight()));
    }
    if(StringUtils.isNotBlank(configuration.getSifaWarn())) {
      map.put(Control.SifaAlarm, Pin.valueOf(configuration.getSifaWarn()));
    }
  }

  /**
   * Registerd method to receive events which are forwared to FT232h.
   */
  @Present
  public void present(Event event) {
    if(map.containsKey(event.getControl())) {
      if(Float.valueOf(1.0F).equals(event.getData().getCurrent())) {
        ft232h.on(map.get(event.getControl()));
      } else {
        ft232h.off(map.get(event.getControl()));
      }
      ft232h.execute();
    }
  }

  /**
   * Initialization will trigger all PINs on and off to verify functionality.
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    ft232h = Ft232h.getInstance();
    logger.info("startup test started");
    Control[] controls = {Control.Pzb55, Control.Pzb70, Control.Pzb85, Control.Pzb40, Control.Pzb500, Control.Pzb1000, Control.SifaLight, Control.SifaAlarm};
    for(Control control:controls) {
      logger.info(String.format("%s ON", control.name()));
      present(getOnEvent(control));
      Thread.sleep(500);
      logger.info(String.format("%s OFF", control.name()));
      present(getOffEvent(control));
      Thread.sleep(500);
    }
    logger.info("startup test finished");
  }

  private Event getOnEvent(Control control) {
    return getEvent(control, 1.0f);
  }

  private Event getOffEvent(Control control) {
    return getEvent(control, 0.0f);
  }

  private Event getEvent(Control control, float value) {
    ControlData data = new ControlData();
    data.setCurrent(value);
    return new Event(control, data);
  }

  @PreDestroy
  public void shutdown() {
    ft232h.shutdown();
  }
}
