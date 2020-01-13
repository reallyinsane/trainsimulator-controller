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
import io.mathan.trainsimulator.model.Locomotive;
import io.mathan.trainsimulator.model.generic.GenericLocomotive;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Main component managing event handling. This component is a {@link Collector} and collects events to forward to TrainSimulator. Scheduled every {@link #RATE_EXECUTION} milliseconds pending events
 * will be forwared to the {@link Connector}. Then updates from the Connector will be sent to the {@link Presenter}.
 */
@Component
public class Service implements Collector, BeanPostProcessor {

  private static final int RATE_EXECUTION = 100;
  private static final int RATE_LOCOMOTIVE = 20000;
  private final Presenter presenter;
  private final Connector connector;
  private Logger logger = LoggerFactory.getLogger(Service.class);
  private Locomotive locomotive;
  private Map<Control, ControlData> data = new HashMap<>();
  private List<Event> events = new ArrayList<>();
  private List<LocoUpdateBeanMethod> locoUpdateBeanMethods = new ArrayList<>();

  public Service(Connector connector, Presenter presenter) {
    this.connector = connector;
    this.presenter = presenter;
  }

  public Locomotive getLocomotive() {
    return locomotive;
  }

  public GenericLocomotive getGenericLocomotive() throws TrainSimulatorException {
    return connector.getGenericLocomotive();
  }

  @Override
  public synchronized void raiseEvent(Event event) {
    this.events.add(event);
  }

  @Scheduled(fixedRate = RATE_EXECUTION)
  public synchronized void execute() throws Exception {
    if (this.locomotive == null) {
      this.locomotive = connector.getLocomotive();
    }
    sendToConnector();
    Map<Control, ControlData> updates = getUpdatesFromConnector();
    this.data.putAll(updates);
    this.presenter.present(updates);
  }

  @Scheduled(fixedRate = RATE_LOCOMOTIVE)
  public synchronized void updateLocomotive() throws TrainSimulatorException {
    this.locomotive = connector.getLocomotive();
    for (LocoUpdateBeanMethod bean : locoUpdateBeanMethods) {
      try {
        bean.method.invoke(bean.bean, locomotive);
      } catch (IllegalAccessException | InvocationTargetException e) {
        logger.error(String.format("Could not update loco %s on component %s", locomotive, bean.bean));
      }
    }
  }

  private void sendToConnector() throws TrainSimulatorException, UnsupportedControlException {
    for (Event event : events) {
      connector.setControlData(event.getControl(), event.getData());
    }
    this.events.clear();
  }

  private Map<Control, ControlData> getUpdatesFromConnector() throws TrainSimulatorException, UnsupportedControlException {
    Map<Control, ControlData> dataToUpdate = new HashMap<>();
    for (Control control : locomotive.getControls()) {
      ControlData oldData = data.get(control);
      ControlData newData = connector.getControlData(control);
      if (!Objects.equals(oldData, newData)) {
        dataToUpdate.put(control, newData);
      }
    }
    return dataToUpdate;
  }

  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    for (Method method : bean.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(LocoUpdate.class)) {
        LocoUpdate annotation = method.getDeclaredAnnotation(LocoUpdate.class);
        if (method.getParameterCount() != 1 || !Locomotive.class.equals(method.getParameterTypes()[0])) {
          throw new FatalBeanException(String.format("Methods annotated with @LocoUpdate need a parameter of type %s", Locomotive.class.getName()));
        }
        LocoUpdateBeanMethod listener = new LocoUpdateBeanMethod();
        listener.bean = bean;
        listener.method = method;
        locoUpdateBeanMethods.add(listener);
      }
    }
    return bean;
  }

  class LocoUpdateBeanMethod {

    Object bean;
    Method method;
  }
}
