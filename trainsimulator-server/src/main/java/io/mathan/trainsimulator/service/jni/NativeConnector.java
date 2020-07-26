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
import io.mathan.trainsimulator.service.jni.Mapping.CombinedMapping;
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
import org.springframework.stereotype.Component;

/**
 * The Connector is responsible for communication with TrainSimulator API via {@link NativeLibrary}. Therefor a {@link NativeLibraryFactory} is necessary to create the API interface.
 */
@Component
public class NativeConnector implements InitializingBean, Connector {

  public static final String DELIMITER_LOCO = ".:.";
  public static final String DELIMITER_CONTROLLER = "::";
  private static final Map<String, Integer> commonControlsMap = new HashMap<>();
  private final Map<String, Integer> controlsMap = new HashMap<>();
  private final Map<String, VirtualControl> virtualControlsMap = new HashMap<>();
  private final Map<String, CombinedControl> combinedControlMap = new HashMap<>();
  private Logger logger = LoggerFactory.getLogger(NativeConnector.class);
  private NativeLibraryFactory factory;
  private NativeLibrary nativeLibrary;

  public static final int ID_CommonCurrentLatitude = 400;
  public static final int ID_CommonCurrentLongitude = 401;
  public static final int ID_CommonFuelLevel = 402;
  public static final int ID_CommonTunnel = 403;
  public static final int ID_CommonGradient = 404;
  public static final int ID_CommonHeading = 405;
  public static final int ID_CommonCurrentTimeHour = 406;
  public static final int ID_CommonCurrentTimeMinute = 407;
  public static final int ID_CommonCurrentTimeSecond = 408;

  static {
    commonControlsMap.put(Control.CommonCurrentLatitude, ID_CommonCurrentLatitude);
    commonControlsMap.put(Control.CommonCurrentLongitude, ID_CommonCurrentLongitude);
    commonControlsMap.put(Control.CommonFuelLevel, ID_CommonFuelLevel);
    commonControlsMap.put(Control.CommonTunnel, ID_CommonTunnel);
    commonControlsMap.put(Control.CommonGradient, ID_CommonGradient);
    commonControlsMap.put(Control.CommonHeading, ID_CommonHeading);
    commonControlsMap.put(Control.CommonCurrentTimeHour, ID_CommonCurrentTimeHour);
    commonControlsMap.put(Control.CommonCurrentTimeMinute, ID_CommonCurrentTimeMinute);
    commonControlsMap.put(Control.CommonCurrentTimeSecond, ID_CommonCurrentTimeSecond);
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
          .addAll(getCombinedControls(defaultMapping, locoMapping));
      locomotive
          .getControls()
          .addAll(commonControlsMap.keySet());
      logger.info("ENGINE {} {} {}", locomotive.getProvider(), locomotive.getProduct(), locomotive.getEngine());
    }
    return locomotive;
  }

  @Override
  public synchronized ControlData getControlData(String control) throws TrainSimulatorException, UnsupportedControlException {
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
      } else {
        CombinedControl combinedControl = combinedControlMap.get(control);
        if (combinedControl != null) {
          float value = 0;
          for (Integer subControlId : combinedControl.getIds()) {
            float subControlValue = this.nativeLibrary.GetControllerValue(subControlId, 0);
            value = value * 10 + subControlValue;
          }
          ControlData controlData = new ControlData();
          controlData.setCurrent(value);
          controlData.setMinimum(0f);
          controlData.setMaximum(value);
          return controlData;
        }
      }
    }
    return null;
  }

  @Override
  public void setControlData(String control, ControlData data) throws TrainSimulatorException, UnsupportedControlException {
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

  private List<String> getCombinedControls(Mapping defaultMapping, Mapping locoMapping) {
    List<String> list = new ArrayList<>();
    synchronized (combinedControlMap) {
      combinedControlMap.clear();
      defaultMapping.getCombinedMapping().forEach(this::checkCombinedControl);
      locoMapping.getCombinedMapping().forEach(this::checkCombinedControl);
    }
    return list;
  }

  private void checkCombinedControl(String controlName, CombinedMapping combinedMapping) {
    List<Integer> ids = new ArrayList<>();
    boolean allAvailable = true;
    for (String control : combinedMapping.getControls()) {
      Integer id = controlsMap.get(control);
      if (id == null) {
        allAvailable = false;
        break;
      }
      ids.add(id);
    }
    if (allAvailable) {
      combinedControlMap.put(controlName, new CombinedControl(ids));
    }
  }

  private List<String> getVirtualControls(Mapping defaultMapping, Mapping locoMapping) {
    List<String> list = new ArrayList<>();
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
              String control = virtualControl.getName();
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

  private List<String> getControls(Mapping defaultMapping, Mapping locoMapping) {
    synchronized (controlsMap) {
      controlsMap.clear();
      List<String> list = new ArrayList<>();
      String result = this.nativeLibrary.GetControllerList();
      if (result != null) {
        StringTokenizer tokenizer = new StringTokenizer(result, DELIMITER_CONTROLLER);
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
          String controlName = tokenizer.nextToken();
          String control = getControlForName(controlName, defaultMapping, locoMapping);
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

  private String getControlForName(
      String controlName, Mapping defaultMapping, Mapping locoMapping) {
    String locoControlName = locoMapping.getSimpleMapping().get(controlName);
    if (locoControlName != null) {
      return locoControlName;
    }
    String defaultControlName = defaultMapping.getSimpleMapping().get(controlName);
    if (defaultControlName != null) {
      return defaultControlName;
    }
    return controlName;
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
            int index3 = line.indexOf("[", index1 + 1);
            if (index3 == -1) {
              // simple mapping
              String key = line.substring(0, index1);
              String value = line.substring(index1 + 1);
              mapping.getSimpleMapping().put(key, value);
            } else {
              String combinedControlName = line.substring(0, index1);
              int index4 = line.indexOf("]", index3 + 3);
              String combined = line.substring(index3 + 1, index4);
              String[] controls = combined.split(",");
              CombinedMapping comnbinedControl = new CombinedMapping(controls);
              mapping.getCombinedMapping().put(combinedControlName, comnbinedControl);
            }
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
