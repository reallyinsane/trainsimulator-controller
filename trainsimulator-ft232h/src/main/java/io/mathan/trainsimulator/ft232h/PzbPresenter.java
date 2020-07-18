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
import javax.annotation.PreDestroy;
import net.sf.yad2xx.FTDIException;
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

  private String time;
  private float hour;
  private float minute;
  private int hundret;
  private int ten;
  private int one;

  private Ft232h ft232h;

  public PzbPresenter() {
  }

  /**
   * Registerd method to receive events which are forwared to FT232h.
   */
  @Present
  public void present(Event event) {
    try {
      boolean controlSet = Float.valueOf(1.0F).equals(event.getData().getCurrent());
      if (Control.Pzb55.equals(event.getControl())) {
        ft232h.setPzb55(controlSet);
      } else if (Control.Pzb70.equals(event.getControl())) {
        ft232h.setPzb70(controlSet);
      } else if (Control.Pzb85.equals(event.getControl())) {
        ft232h.setPzb85(controlSet);
      } else if (Control.Pzb40.equals(event.getControl())) {
        ft232h.setPzb40(controlSet);
      } else if (Control.Pzb500.equals(event.getControl())) {
        ft232h.setPzb500(controlSet);
      } else if (Control.Pzb1000.equals(event.getControl())) {
        ft232h.setPzb1000(controlSet);
      } else if (Control.SifaLight.equals(event.getControl())) {
        ft232h.setSifaLight(controlSet);
      } else if (Control.SifaAlarm.equals(event.getControl())) {
        ft232h.setSifaBuzzer(controlSet);
      } else if (Control.CommonCurrentTimeHour.equals(event.getControl())) {
        hour = event.getData().getCurrent();
        time = String.format("%02d:%02d", (int) hour, (int) minute);
        ft232h.setBlueTime(time);
      } else if (Control.CommonCurrentTimeMinute.equals(event.getControl())) {
        minute = event.getData().getCurrent();
        time = String.format("%02d:%02d", (int) hour, (int) minute);
        ft232h.setBlueTime(time);
      } else if ("SpeedometerKPH".equals(event.getControl())) {
        ft232h.setWhite(event.getData().getCurrent());
      } else if ("LZB_Buzzer".equals(event.getControl())) {
//        ft232h.setLZBBuzzer(controlSet);
      } else if ("TargetSpeed100".equals(event.getControl())) {
        hundret = event.getData().getCurrent().intValue();
        ft232h.setRed(1f * (hundret * 100 + ten * 10 + one));
      } else if ("TargetSpeed10".equals(event.getControl())) {
        ten = event.getData().getCurrent().intValue();
        ft232h.setRed(1f * (hundret * 100 + ten * 10 + one));
      } else if ("TargetSpeed1".equals(event.getControl())) {
        one = event.getData().getCurrent().intValue();
        ft232h.setRed(1f * (hundret * 100 + ten * 10 + one));
      } else if ("RawTargetDistance".equals(event.getControl())) {
        ft232h.setBar(event.getData().getCurrent() / 4000);
      }
      ft232h.delay(10);
    } catch (FTDIException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initialization will trigger all PINs on and off to verify functionality.
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    ft232h = Ft232h.getInstance();
//    logger.info("startup test started");
//    String[] controls = {Control.Pzb55, Control.Pzb70, Control.Pzb85, Control.Pzb40, Control.Pzb500, Control.Pzb1000, Control.SifaLight, Control.SifaAlarm};
//    for (String control : controls) {
//      logger.info(String.format("%s ON", control));
//      present(getOnEvent(control));
//      Thread.sleep(500);
//      logger.info(String.format("%s OFF", control));
//      present(getOffEvent(control));
//      Thread.sleep(500);
//    }
//    logger.info("startup test finished");
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

  @PreDestroy
  public void shutdown() {
    ft232h.shutdown();
  }
}
