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

package io.mathan.trainsimulator.service.jni;

import io.mathan.trainsimulator.model.Control;
import io.mathan.trainsimulator.model.ControlData;
import io.mathan.trainsimulator.model.Locomotive;
import io.mathan.trainsimulator.model.generic.GenericControl;
import io.mathan.trainsimulator.model.generic.GenericLocomotive;
import io.mathan.trainsimulator.service.Connector;
import io.mathan.trainsimulator.service.TrainSimulatorException;
import io.mathan.trainsimulator.service.UnsupportedControlException;
import io.mathan.trainsimulator.service.jni.Mapping.VirtualMapping;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * The Connector is responsible for communication with TrainSimulator API via {@link NativeLibrary}. Therefor a {@link NativeLibraryFactory} is necessary to create the API interface.
 */
@Component
@Profile("native")
public class NativeConnector implements InitializingBean, Connector {

  private static final String DELIMITER_LOCO = ".:.";
  private static final String DELIMITER_CONTROLLER = "::";
  private static final Map<Control, Integer> commonControlsMap = new HashMap<>();
  private final Map<Control, Integer> controlsMap = new HashMap<>();
  private final Map<Control, VirtualControl> virtualControlsMap = new HashMap<>();
  private Logger logger = LoggerFactory.getLogger(NativeConnector.class);
  private NativeLibraryFactory factory;
  private NativeLibrary nativeLibrary;

  static {
    commonControlsMap.put(Control.CommonCurrentLatitude, 400);
    commonControlsMap.put(Control.CommonCurrentLongitude, 401);
    commonControlsMap.put(Control.CommonFuelLevel, 402);
    commonControlsMap.put(Control.CommonTunnel, 403);
    commonControlsMap.put(Control.CommonGradient, 404);
    commonControlsMap.put(Control.CommonHeading, 405);
    commonControlsMap.put(Control.CommonCurrentTimeHour, 406);
    commonControlsMap.put(Control.CommonCurrentTimeMinute, 407);
    commonControlsMap.put(Control.CommonCurrentTimeSecond, 408);
  }

  public NativeConnector(NativeLibraryFactory factory) {
    this.factory = factory;
  }

  @Override
  public Locomotive getLocomotive() throws TrainSimulatorException {
    Locomotive locomotive = new Locomotive();
    StringTokenizer tokenizer = new StringTokenizer(this.nativeLibrary.GetLocoName(), DELIMITER_LOCO);
    if (tokenizer.hasMoreTokens()) {
      locomotive.setProvider(tokenizer.nextToken());
      locomotive.setProduct(tokenizer.nextToken());
      locomotive.setEngine(tokenizer.nextToken());
      locomotive.setCombinedThrottleBrake(this.nativeLibrary.GetRailSimCombinedThrottleBrake());
      Mapping defaultMapping = getMapping("default");
      Mapping locoMapping = getMapping(locomotive.getEngine());
      locomotive
          .getControls()
          .addAll(getControls(defaultMapping, locoMapping));
      locomotive
          .getControls()
          .addAll(getVirtualControls(defaultMapping, locoMapping));
      locomotive
          .getControls()
          .addAll(commonControlsMap.keySet());
      logger.info("ENGINE {} {} {}", locomotive.getProvider(), locomotive.getProduct(), locomotive.getEngine());
    }
    return locomotive;
  }

  @Override
  public synchronized ControlData getControlData(Control control) throws TrainSimulatorException, UnsupportedControlException {
    Integer id = commonControlsMap.get(control);
    if (id == null) {
      id = controlsMap.get(control);
    }
    if (id != null) {
      ControlData value = new ControlData();
      value.setCurrent(this.nativeLibrary.GetControllerValue(id, 0));
      value.setMinimum(this.nativeLibrary.GetControllerValue(id, 1));
      value.setMaximum(this.nativeLibrary.GetControllerValue(id, 2));
      return value;
    } else {
      VirtualControl virtualControl = virtualControlsMap.get(control);
      if (virtualControl != null) {
        ControlData value = new ControlData();
        value.setMinimum(0f);
        value.setMaximum(1f);
        Float current = this.nativeLibrary.GetControllerValue(virtualControl.getId(), 0);
        if (virtualControl.getValue().equals(current)) {
          value.setCurrent(1f);
        } else {
          value.setCurrent(0f);
        }
        return value;
      }
    }
    return null;
  }

  @Override
  public void setControlData(Control control, ControlData data) throws TrainSimulatorException, UnsupportedControlException {
    synchronized (controlsMap) {
      Integer id = controlsMap.get(control);
      if (id != null) {
        this.nativeLibrary.SetControllerValue(id, data.getCurrent());
      } else {
        synchronized (virtualControlsMap) {
          VirtualControl virtualControl = virtualControlsMap.get(control);
          if (virtualControl != null) {
            this.nativeLibrary.SetControllerValue(virtualControl.getId(), data.getCurrent());
          }
        }
      }
    }
  }

  @Override
  public GenericLocomotive getGenericLocomotive() throws TrainSimulatorException {
    GenericLocomotive locomotive = new GenericLocomotive();
    StringTokenizer tokenizer = new StringTokenizer(this.nativeLibrary.GetLocoName(), DELIMITER_LOCO);
    if (tokenizer.hasMoreTokens()) {
      locomotive.setProvider(tokenizer.nextToken());
      locomotive.setProduct(tokenizer.nextToken());
      locomotive.setEngine(tokenizer.nextToken());
      locomotive.setCombinedThrottleBrake(this.nativeLibrary.GetRailSimCombinedThrottleBrake());
      locomotive.getControls().addAll(getGenericControls());
    }
    return locomotive;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.nativeLibrary = factory.getInstance();
  }

  private List<GenericControl> getGenericControls() {
    List<GenericControl> list = new ArrayList<>();
    String result = this.nativeLibrary.GetControllerList();
    if (result != null) {
      StringTokenizer tokenizer = new StringTokenizer(result, DELIMITER_CONTROLLER);
      int index = 0;
      while (tokenizer.hasMoreTokens()) {
        String controlName = tokenizer.nextToken();
        GenericControl control = new GenericControl();
        control.setCurrent(this.nativeLibrary.GetControllerValue(index, 0));
        control.setId(index);
        control.setName(controlName);
        control.setMaximum(this.nativeLibrary.GetControllerValue(index, 2));
        control.setMinimum(this.nativeLibrary.GetControllerValue(index, 1));
        list.add(control);
        index++;
      }
    }
    return list;
  }

  private List<Control> getVirtualControls(Mapping defaultMapping, Mapping locoMapping) {
    List<Control> list = new ArrayList<>();
    synchronized (virtualControlsMap) {
      virtualControlsMap.clear();
      String result = this.nativeLibrary.GetControllerList();
      if (result != null) {
        StringTokenizer tokenizer = new StringTokenizer(result, DELIMITER_CONTROLLER);
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
          String controlName = tokenizer.nextToken();
          List<VirtualMapping> virtualControls =
              defaultMapping.getVirtualMapping().get(controlName);
          if (virtualControls != null) {
            for (VirtualMapping virtualControl : virtualControls) {
              Control control = Control.fromString(virtualControl.getName());
              if (control != null) {
                list.add(control);
                VirtualControl vc = new VirtualControl(index, virtualControl.getValue());
                virtualControlsMap.put(control, vc);
              }
            }
          }
          index++;
        }
      }
    }
    return list;
  }

  private List<Control> getControls(Mapping defaultMapping, Mapping locoMapping) {
    synchronized (controlsMap) {
      controlsMap.clear();
      List<Control> list = new ArrayList<>();
      String result = this.nativeLibrary.GetControllerList();
      if (result != null) {
        StringTokenizer tokenizer = new StringTokenizer(result, DELIMITER_CONTROLLER);
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
          String controlName = tokenizer.nextToken();
          Control control = getControlForName(controlName, defaultMapping, locoMapping);
          if (control != null) {
            list.add(control);
            controlsMap.put(control, index);
          }
          index++;
        }
      }
      return list;
    }
  }

  private Control getControlForName(
      String controlName, Mapping defaultMapping, Mapping locoMapping) {
    Control control = Control.fromString(controlName);
    if (control != null) {
      return control;
    }
    String locoControlName = locoMapping.getSimpleMapping().get(controlName);
    if (locoControlName != null) {
      control = Control.fromString(locoControlName);
      if (control != null) {
        return control;
      }
    }
    String defaultControlName = defaultMapping.getSimpleMapping().get(controlName);
    if (defaultControlName != null) {
      control = Control.fromString(defaultControlName);
      if (control != null) {
        return control;
      }
    }
    return null;
  }

  private Mapping loadMapping(InputStream in) {
    Mapping mapping = new Mapping();
    try {
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(in));
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("#")) {
          continue;
        }
        int index1 = line.indexOf('=');
        if (index1 != -1) {
          int index2 = line.indexOf('=', index1 + 1);
          if (index2 == -1) {
            // simple mapping
            String key = line.substring(0, index1);
            String value = line.substring(index1 + 1);
            mapping.getSimpleMapping().put(key, value);
          } else {
            String virtualControlName = line.substring(0, index1);
            Float value = Float.valueOf(line.substring(index1 + 1, index2));
            String controlName = line.substring(index2 + 1);
            VirtualMapping virtualControl = new VirtualMapping(controlName, value);

            List<VirtualMapping> list = mapping.getVirtualMapping().computeIfAbsent(virtualControlName, k -> new ArrayList<>());
            list.add(virtualControl);
          }
        }
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return mapping;
  }

  private Mapping getMapping(String loco) {
    Mapping mapping = new Mapping();
    File file = new File(loco + ".mapping");
    InputStream in;
    if (file.exists()) {
      logger.info("using mapping found in {}", file.getAbsolutePath());
      try {
        in = new FileInputStream(file);
        return loadMapping(in);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    in = NativeConnector.class.getResourceAsStream("/" + loco + ".mapping");
    if (in != null) {
      logger.info("using mapping from classpath for {}.mapping", loco);
      return loadMapping(in);
    }

    return mapping;
  }
}
