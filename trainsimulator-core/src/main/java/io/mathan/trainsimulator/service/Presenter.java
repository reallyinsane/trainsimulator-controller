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

import io.mathan.trainsimulator.model.ControlData;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * The Presenter component is used to delegate updates to certain controls to component methods annotated with {@link Present}. All possible component methods are detected using a {@link
 * BeanPostProcessor}.
 */
@Component
public class Presenter implements BeanPostProcessor {

  private Logger logger = LoggerFactory.getLogger(Presenter.class);

  private List<PresentAnnotatedBeanMethod> anyControl = new ArrayList<>();
  private Map<String, List<PresentAnnotatedBeanMethod>> specificControl = new HashMap<>();

  /**
   * Initiates an update of the presentation for the given controls.
   *
   * @param updates The controls with their new values.
   */
  public void present(Map<String, ControlData> updates) {
    for (String control : updates.keySet()) {
      present(control, updates.get(control));
    }
  }

  /**
   * Initiates an update of the presentation for the given control.
   *
   * @param control The control.
   * @param data The new value.
   */
  public void present(String control, ControlData data) {
    List<PresentAnnotatedBeanMethod> beans = specificControl.get(control);
    Event event = new Event(control, data);
    if (beans != null) {
      for (PresentAnnotatedBeanMethod bean : beans) {
        try {
          bean.method.invoke(bean.bean, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
          logger.error(String.format("Could not present event for control %s on component %s", control, bean.bean), e);
        }
      }
    }
    for (PresentAnnotatedBeanMethod bean : anyControl) {
      try {
        bean.method.invoke(bean.bean, event);
      } catch (IllegalAccessException | InvocationTargetException e) {
        logger.error(String.format("Could not present event for control %s on component %s", control, bean.bean), e);
      }
    }
  }

  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    for (Method method : bean.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(Present.class)) {
        Present annotation = method.getDeclaredAnnotation(Present.class);
        if (method.getParameterCount() != 1 || !Event.class.equals(method.getParameterTypes()[0])) {
          throw new FatalBeanException(String.format("Methods annotated with @Present need a parameter of type %s", Event.class.getName()));
        }
        for (String control : annotation.controls()) {
          addMethod(control, bean, method);
        }
        if (annotation.controls().length == 0) {
          PresentAnnotatedBeanMethod listener = new PresentAnnotatedBeanMethod();
          listener.bean = bean;
          listener.method = method;
          anyControl.add(listener);
          logger.info("@Present for {}.{}", bean.getClass().getName(), method.getName());
        }
      }
    }
    return bean;
  }

  /**
   * Adds a components method to the registered methods for a certain control.
   *
   * @param control The control the component method is registered for.
   * @param bean The component.
   * @param method The annotated method.
   */
  private void addMethod(String control, Object bean, Method method) {
    List<PresentAnnotatedBeanMethod> beans = specificControl.computeIfAbsent(control, k -> new ArrayList<>());
    PresentAnnotatedBeanMethod listener = new PresentAnnotatedBeanMethod();
    listener.bean = bean;
    listener.method = method;
    beans.add(listener);
  }

  /**
   * Identifies a certain bean method annotated with {@link Present}.
   */
  class PresentAnnotatedBeanMethod {

    Object bean;
    Method method;
  }
}
