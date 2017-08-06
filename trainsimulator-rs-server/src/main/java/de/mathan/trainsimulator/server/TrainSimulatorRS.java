package de.mathan.trainsimulator.server;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.mathan.trainsimulator.TrainSimulatorService;
import de.mathan.trainsimulator.model.Controller;
import de.mathan.trainsimulator.model.ControllerValue;
import de.mathan.trainsimulator.model.Locomotive;
import de.mathan.trainsimulator.model.generic.GenericController;
import de.mathan.trainsimulator.model.generic.GenericLocomotive;
import de.mathan.trainsimulator.server.internal.Mapping;
import de.mathan.trainsimulator.server.internal.NativeLibrary;
import de.mathan.trainsimulator.server.internal.VirtualController;

/**
 * REST-Service delegating the requests to Railworks.dll using JNA. 
 * @author Matthias Hanisch
 */
@Path("/trainsimulator")
public class TrainSimulatorRS implements TrainSimulatorService{
  @Inject
  private NativeLibrary nativeLibrary;
  
  private static Map<String, Integer> nameMap = new HashMap<>();
  private static Map<Controller, Integer> controllerMap = new HashMap<>();
  private static Map<Controller, VirtualController> virtualControllerMap= new HashMap<>();
  
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
      locomotive.getController().addAll(getController(getMapping("default"), getMapping(locomotive.getEngine())));
      locomotive.getController().addAll(getVirtualController());
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
      locomotive.getController().addAll(getGenericController());
    }
    return locomotive;
  }
  
  private List<GenericController> getGenericController() {
    List<GenericController> list = new ArrayList<>();
    String result=nativeLibrary.GetControllerList();
    if(result!=null) {
      StringTokenizer tokenizer = new StringTokenizer(result, "::");
      int index = 0;
      while (tokenizer.hasMoreTokens()) {
        String controllerName = tokenizer.nextToken();
        GenericController controller = new GenericController();
        controller.setCurrent(nativeLibrary.GetControllerValue(index, 0));
        controller.setId(index);
        controller.setName(controllerName);
        controller.setMaximum(nativeLibrary.GetControllerValue(index, 2));
        controller.setMinimum(nativeLibrary.GetControllerValue(index, 1));
        list.add(controller);
        index++;
      }   
    }
    return list;
  }

  private List<Controller> getVirtualController() {
    List<Controller> list = new ArrayList<>();
    synchronized (virtualControllerMap) {
      virtualControllerMap.clear();
      File file = new File("virtual-controller.mapping");
      if(file.exists()) {
        try {
          BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
          String line = null;
          while((line=reader.readLine())!=null) {
            if(line.startsWith("#")) {
              continue;
            }
            int index1 = line.indexOf('=');
            int index2 = line.lastIndexOf('=');
            if(index1!=-1&&index2!=-1&&index1!=index2) {
              String virtualControllerName =line.substring(0,index1);
              Float value = Float.valueOf(line.substring(index1+1, index2));
              Controller controller = Controller.fromString(line.substring(index2+1));
              if(controller!=null) {
                Integer id = nameMap.get(virtualControllerName);
                if(id!=null) {
                  VirtualController virtualController = new VirtualController(id,value);
                  list.add(controller);
                  virtualControllerMap.put(controller, virtualController);
                }
              }
            }
          }
          reader.close();
        } catch (NumberFormatException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return list;
  }
  
  @GET
  @Path("/controller/{controller}")
  @Produces(MediaType.APPLICATION_JSON)
  public ControllerValue getControllerValue(@PathParam("controller") Controller controller) {
    synchronized (controllerMap) {
      Integer id = controllerMap.get(controller);
      if(id!=null) {
        ControllerValue value = new ControllerValue();
        value.setCurrent(nativeLibrary.GetControllerValue(id, 0));
        value.setMinimum(nativeLibrary.GetControllerValue(id, 1));
        value.setMaximum(nativeLibrary.GetControllerValue(id, 2));
        return value;
      } else {
        synchronized (virtualControllerMap) {
          VirtualController virtualController = virtualControllerMap.get(controller);
          if(virtualController!=null) {
            ControllerValue value = new ControllerValue();
            value.setMinimum(0f);
            value.setMaximum(1f);
            Float current = nativeLibrary.GetControllerValue(virtualController.getId(), 0);
            if(virtualController.getValue().equals(current)) {
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
  
	private Mapping getMapping(@QueryParam("loco") String loco) {
	  Mapping mapping = new Mapping();
	  mapping.setName(loco);
    File file = new File(loco+".mapping");
    if(file.exists()) {
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line=null;
        while((line=reader.readLine())!=null) {
          if(line.startsWith("#")) {
            continue;
          }
          int index =line.indexOf('=');
          if(index!=-1) {
            String key = line.substring(0,index);
            String value = line.substring(index+1);
            mapping.getEntries().put(key, value);
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
	
	private List<Controller> getController(Mapping defaultMapping, Mapping locoMapping) {
	  synchronized (controllerMap) {
	    controllerMap.clear();
	    nameMap.clear();
	    List<Controller> list = new ArrayList<>();
      String result=nativeLibrary.GetControllerList();
      if(result!=null) {
        StringTokenizer tokenizer = new StringTokenizer(result, "::");
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
          String controllerName = tokenizer.nextToken();
          nameMap.put(controllerName, index);
          Controller controller = getControllerForName(controllerName, defaultMapping, locoMapping); 
          if(controller!=null) {
            list.add(controller);
            controllerMap.put(controller, index);
          }
          index++;
        }   
      }
      return list;
    }
	}
    
  private Controller getControllerForName(String controllerName, Mapping defaultMapping,
      Mapping locoMapping) {
    Controller controller = Controller.fromString(controllerName);
    if(controller!=null) {
      return controller;
    }
    String locoControllerName = locoMapping.getEntries().get(controllerName);
    if(locoControllerName!=null) {
      controller= Controller.fromString(locoControllerName);
      if(controller!=null) {
        return controller;
      }
    }
    String defaultControllerName = defaultMapping.getEntries().get(controllerName);
    if(defaultControllerName!=null) {
      controller= Controller.fromString(defaultControllerName);
      if(controller!=null) {
        return controller;
      }
    }
    return null;
  }

}
