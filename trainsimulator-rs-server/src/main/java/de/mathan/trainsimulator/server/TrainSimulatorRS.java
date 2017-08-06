package de.mathan.trainsimulator.server;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.mathan.trainsimulator.TrainSimulatorService;
import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.ControlValue;
import de.mathan.trainsimulator.model.Locomotive;
import de.mathan.trainsimulator.model.generic.GenericControl;
import de.mathan.trainsimulator.model.generic.GenericLocomotive;
import de.mathan.trainsimulator.server.internal.Mapping;
import de.mathan.trainsimulator.server.internal.Mapping.VirtualMapping;
import de.mathan.trainsimulator.server.internal.NativeLibrary;
import de.mathan.trainsimulator.server.internal.VirtualControl;

/**
 * REST-Service delegating the requests to Railworks.dll using JNA. 
 * @author Matthias Hanisch
 */
@Path("/trainsimulator")
public class TrainSimulatorRS implements TrainSimulatorService{
  @Inject
  private NativeLibrary nativeLibrary;
  
  private static Map<String, Integer> nameMap = new HashMap<>();
  private static Map<Control, Integer> controlsMap = new HashMap<>();
  private static Map<Control, VirtualControl> virtualControlsMap= new HashMap<>();
  
  @GET
  @Path("/locomotive")
  @Produces(MediaType.APPLICATION_JSON)
  public Locomotive getLocomotive() {
    Locomotive locomotive = new Locomotive();
    StringTokenizer tokenizer = new StringTokenizer(nativeLibrary.GetLocoName(), ".:.");
    if(tokenizer.hasMoreTokens()) {
      locomotive.setProvider(tokenizer.nextToken());
      locomotive.setProduct(tokenizer.nextToken());
      locomotive.setEngine(tokenizer.nextToken());
      locomotive.setCombinedThrottleBrake(nativeLibrary.GetRailSimCombinedThrottleBrake());
      locomotive.getControls().addAll(getControls(getMapping("default"), getMapping(locomotive.getEngine())));
      locomotive.getControls().addAll(getVirtualControls(getMapping("default"), getMapping(locomotive.getEngine())));
    }
    return locomotive;
  }
  
  @GET
  @Path("/generic")
  @Produces(MediaType.APPLICATION_JSON)
  public GenericLocomotive getGenericLocomotive() {
    GenericLocomotive locomotive = new GenericLocomotive();
    StringTokenizer tokenizer = new StringTokenizer(nativeLibrary.GetLocoName(), ".:.");
    if(tokenizer.hasMoreTokens()) {
      locomotive.setProvider(tokenizer.nextToken());
      locomotive.setProduct(tokenizer.nextToken());
      locomotive.setEngine(tokenizer.nextToken());
      locomotive.setCombinedThrottleBrake(nativeLibrary.GetRailSimCombinedThrottleBrake());
      locomotive.getControls().addAll(getGenericControls());
    }
    return locomotive;
  }
  
  private List<GenericControl> getGenericControls() {
    List<GenericControl> list = new ArrayList<>();
    String result=nativeLibrary.GetControllerList();
    if(result!=null) {
      StringTokenizer tokenizer = new StringTokenizer(result, "::");
      int index = 0;
      while (tokenizer.hasMoreTokens()) {
        String controlName = tokenizer.nextToken();
        GenericControl control = new GenericControl();
        control.setCurrent(nativeLibrary.GetControllerValue(index, 0));
        control.setId(index);
        control.setName(controlName);
        control.setMaximum(nativeLibrary.GetControllerValue(index, 2));
        control.setMinimum(nativeLibrary.GetControllerValue(index, 1));
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
      String result=nativeLibrary.GetControllerList();
      if(result!=null) {
        StringTokenizer tokenizer = new StringTokenizer(result, "::");
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
          String controlName = tokenizer.nextToken();
          List<VirtualMapping> virtualControls = defaultMapping.getVirtualMapping().get(controlName);
          if(virtualControls!=null) {
            for(VirtualMapping virtualControl:virtualControls) {
              Control control = Control.fromString(virtualControl.getName());
              if(control!=null) {
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
  
  @GET
  @Path("/control/{control}")
  @Produces(MediaType.APPLICATION_JSON)
  public ControlValue getControlValue(@PathParam("control") Control control) {
    synchronized (controlsMap) {
      Integer id = controlsMap.get(control);
      if(id!=null) {
        ControlValue value = new ControlValue();
        value.setCurrent(nativeLibrary.GetControllerValue(id, 0));
        value.setMinimum(nativeLibrary.GetControllerValue(id, 1));
        value.setMaximum(nativeLibrary.GetControllerValue(id, 2));
        return value;
      } else {
        synchronized (virtualControlsMap) {
          VirtualControl virtualControl = virtualControlsMap.get(control);
          if(virtualControl!=null) {
            ControlValue value = new ControlValue();
            value.setMinimum(0f);
            value.setMaximum(1f);
            Float current = nativeLibrary.GetControllerValue(virtualControl.getId(), 0);
            if(virtualControl.getValue().equals(current)) {
              value.setCurrent(1f);
            } else {
              value.setCurrent(0f);
            }
            return value;
          }
        }
      }
      return null;
    }
  }
  
	private Mapping getMapping(String loco) {
	  Mapping mapping = new Mapping();
    File file = new File(loco+".mapping");
    if(file.exists()) {
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line=null;
        while((line=reader.readLine())!=null) {
          if(line.startsWith("#")) {
            continue;
          }
          int index1 =line.indexOf('=');
          if(index1!=-1) {
            int index2 = line.indexOf('=', index1+1);
            if(index2==-1) {
              // simple mapping
              String key = line.substring(0,index1);
              String value = line.substring(index1+1);
              mapping.getSimpleMapping().put(key, value);
            } else {
              String virtualControlName =line.substring(0,index1);
              Float value = Float.valueOf(line.substring(index1+1, index2));
              String controlName = line.substring(index2+1);
              VirtualMapping virtualControl = new VirtualMapping(controlName, value);
              
              List<VirtualMapping> list = mapping.getVirtualMapping().get(virtualControlName);
              if(list==null) {
                list = new ArrayList<>();
                mapping.getVirtualMapping().put(virtualControlName, list);
              }
              list.add(virtualControl);
            }
          }
        }
        reader.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return mapping;
	}
	
	private List<Control> getControls(Mapping defaultMapping, Mapping locoMapping) {
	  synchronized (controlsMap) {
	    controlsMap.clear();
	    nameMap.clear();
	    List<Control> list = new ArrayList<>();
      String result=nativeLibrary.GetControllerList();
      if(result!=null) {
        StringTokenizer tokenizer = new StringTokenizer(result, "::");
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
          String controlName = tokenizer.nextToken();
          nameMap.put(controlName, index);
          Control control = getControlForName(controlName, defaultMapping, locoMapping); 
          if(control!=null) {
            list.add(control);
            controlsMap.put(control, index);
          }
          index++;
        }   
      }
      return list;
    }
	}
    
  private Control getControlForName(String controlName, Mapping defaultMapping,
      Mapping locoMapping) {
    Control control = Control.fromString(controlName);
    if(control!=null) {
      return control;
    }
    String locoControlName = locoMapping.getSimpleMapping().get(controlName);
    if(locoControlName!=null) {
      control= Control.fromString(locoControlName);
      if(control!=null) {
        return control;
      }
    }
    String defaultControlName = defaultMapping.getSimpleMapping().get(controlName);
    if(defaultControlName!=null) {
      control= Control.fromString(defaultControlName);
      if(control!=null) {
        return control;
      }
    }
    return null;
  }

}
