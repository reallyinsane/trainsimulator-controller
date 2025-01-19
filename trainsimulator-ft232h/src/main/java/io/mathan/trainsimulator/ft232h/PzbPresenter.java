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

import io.mathan.adafruit.Bargraph.Color;
import io.mathan.trainsimulator.model.Control;
import io.mathan.trainsimulator.model.ControlData;
import io.mathan.trainsimulator.service.Controller;
import io.mathan.trainsimulator.service.Event;
import io.mathan.trainsimulator.service.Present;
import javax.annotation.PreDestroy;

import io.mathan.trainsimulator.service.Presenter;
import net.sf.yad2xx.FTDIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
  private float hour = 0f;
  private float minute = 0f;

  private int DELAY = 20;

  private Ft232hRebuild ft232h;
//  @Autowired
//  private Controller presenter;

  public PzbPresenter() {
  }

  @Present(Control.Pzb55)
  public void pzb55(Event event) throws FTDIException {
    ft232h.setPzb55(toBoolean(event));
  }

  @Present(Control.Pzb70)
  public void pzb70(Event event) throws FTDIException {
    ft232h.setPzb70(toBoolean(event));

  }

  @Present(Control.Pzb85)
  public void pzb85(Event event) throws FTDIException {
    ft232h.setPzb85(toBoolean(event));
  }

  @Present(Control.Pzb40)
  public void pzb40(Event event) throws FTDIException {
    ft232h.setPzb40(toBoolean(event));
  }

  @Present(Control.Pzb500)
  public void pzb500(Event event) throws FTDIException {
    ft232h.setPzb500(toBoolean(event));
  }

  @Present(Control.Pzb1000)
  public void pzb1000(Event event) throws FTDIException {
    ft232h.setPzb1000(toBoolean(event));
  }

  @Present(Control.SifaLight)
  public void sifaLight(Event event) throws FTDIException {
    ft232h.setSifaLight(toBoolean(event));
  }

  @Present(Control.SifaAlarm)
  public void sifaAlarm(Event event) throws FTDIException {
    ft232h.setSifaBuzzer(toBoolean(event));
  }

  @Present({Control.CommonCurrentTimeHour, Control.CommonCurrentTimeMinute})
  public void time(Event event) throws FTDIException {
    if (isTopUp()) {
      if (Control.CommonCurrentTimeHour.equals(event.getControl())) {
        hour = event.getData().getCurrent();
      } else {
        minute = event.getData().getCurrent();
      }
      time = String.format("%02d:%02d", (int) hour, (int) minute);
      ft232h.setBlueTime(time);
    }
  }

  @Present(Control.SpeedometerKPH)
  public void speed(Event event) throws FTDIException {
    ft232h.setWhite(event.getData().getCurrent());
  }

  @Present(Control.LZBBuzzer)
  public void lzbWarning(Event event) throws FTDIException {
    ft232h.setLZBBuzzer(toBoolean(event));
  }

  @Present({"VSoll", "AFBSpeed"})
  public void currentMaxSpeed(Event event) throws FTDIException {
    if (isFrontUp()) {
      ft232h.setRed(event.getData().getCurrent());
    }
  }

  @Present({Control.RawSpeedTarget, "TargetSpeed"})
  public void targetSpeed(Event event) throws FTDIException {
    if (isFrontDown()) {
      ft232h.setRed(event.getData().getCurrent());
    }
  }

  @Present(Control.RawTargetDistance)

  public void lzbDistance(Event event) throws FTDIException {
    Float distance = event.getData().getCurrent();
    Color color = distance > 4000 ? Color.GREEN : (distance > 1000 ? Color.YELLOW : Color.RED);
    ft232h.setBar(distance / 4000
        , color);
    if (isTopDown()) {
      ft232h.setBlue(distance.intValue());
    }
  }

  private boolean isTopUp() throws FTDIException {
    return ft232h.isTop1();
  }

  private boolean isTopDown() throws FTDIException {
    return ft232h.isTop2();
  }

  private boolean isFrontUp() throws FTDIException {
    return ft232h.isFront1();
  }

  private boolean isFrontDown() throws FTDIException {
    return ft232h.isFront2();
  }

  private boolean toBoolean(Event event) {
    return Float.valueOf(1.0f).equals(event.getData().getCurrent());
  }


  /**
   * Initialization will trigger all PINs on and off to verify functionality.
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    ft232h = Ft232hRebuild.getInstance();
//    logger.info("startup test started");
//    String[] controls = {Control.Pzb55, Control.Pzb70, Control.Pzb85, Control.Pzb40, Control.Pzb500, Control.Pzb1000, Control.SifaLight, Control.SifaAlarm};
//    for (String control : controls) {
//      logger.info(String.format("%s ON", control));
//      presenter.present(getOnEvent(control));
//      Thread.sleep(500);
//      logger.info(String.format("%s OFF", control));
//      presenter.present(getOffEvent(control));
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
