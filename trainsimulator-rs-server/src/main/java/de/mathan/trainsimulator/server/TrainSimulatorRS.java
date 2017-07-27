package de.mathan.trainsimulator.server;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.mathan.trainsimulator.TrainSimulatorService;
import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.ControlList;
import de.mathan.trainsimulator.model.Mapping;
import de.mathan.trainsimulator.model.TrainSimulator;
import de.mathan.trainsimulator.server.internal.NativeLibrary;

/**
 * REST-Service delegating the requests to Railworks.dll using JNA. 
 * @author Matthias Hanisch
 */
@Path("/trainsimulator")
public class TrainSimulatorRS implements TrainSimulatorService{
  @Inject
  private NativeLibrary nativeLibrary;
  
  private static  Map<Integer, Control> cacheMap = new HashMap<>();
  
  @GET
  @Path("/info")
  @Produces(MediaType.APPLICATION_JSON)
  public TrainSimulator getInfo() {
    TrainSimulator ts = new TrainSimulator();
    ts.setCombindedThrottleBrake(nativeLibrary.GetRailSimCombinedThrottleBrake());
    ts.setLocoName(nativeLibrary.GetLocoName());
    ts.getControls().addAll(getControls().getControls());
    return ts;
  }

	@GET
	@Path("/list")
	@Deprecated
	public String getControllerList() {
		return nativeLibrary.GetControllerList();
	}
	
	@GET
	@Path("/controls")
	@Produces(MediaType.APPLICATION_JSON)
	public ControlList getControls() {
	  synchronized (cacheMap) {
	    cacheMap.clear();
	    ControlList list = new ControlList();
	    String result=nativeLibrary.GetControllerList();
	    if(result!=null) {
	      StringTokenizer tokenizer = new StringTokenizer(result, "::");
	      int index = 0;
	      while (tokenizer.hasMoreTokens()) {
	        String token = tokenizer.nextToken();
	        Control c = new Control();
	        int id = index++;
	        c.setId(id);
	        c.setName(token);
	        list.getControls().add(c);
	        cacheMap.put(id, c);
	      }	  
	    }
	    return list;
      
    }
	}

  @GET
  @Path("/control/{controllerId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Control getControl(@PathParam("controllerId") int controller) {
    Control control;
    synchronized (cacheMap) {
      control=cacheMap.get(controller);
    }
    if(control==null) {
      return null;
    }
    control.setId(controller);
    control.setCurrent(nativeLibrary.GetControllerValue(controller, 0));
    control.setMinimum(nativeLibrary.GetControllerValue(controller, 1));
    control.setMaximum(nativeLibrary.GetControllerValue(controller, 2));
    return control;
  }
	
	@GET
	@Path("/controller/{controllerId}")
  @Deprecated
	public String getControllerValue(@PathParam("controllerId") int controller, @QueryParam("type") int type) {
		return String.valueOf(nativeLibrary.GetControllerValue(controller, type));
	}

	@PUT
	@Path("/controller/{controllerId}")
  @Deprecated
	public void setControllerValue(@PathParam("controllerId") int controller, @QueryParam("value") float value) {
		nativeLibrary.SetControllerValue(controller, value);
	}
	
	@GET
	@Path("/map")
	@Produces(MediaType.APPLICATION_JSON)
	public Mapping getMapping(@QueryParam("loco") String loco) {
	  Mapping mapping = new Mapping();
	  mapping.setName(loco);
    File file = new File(loco+".mapping");
    if(file.exists()) {
      try {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        for(Object key:props.keySet()) {
          mapping.getEntries().put((String)key, (String)props.get(key));
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return mapping;
	}
    
	@GET
	@Path("/mapping")
  @Deprecated
	public String getMappingOld(@QueryParam("loco") String loco) {
	  File file = new File(loco+".mapping");
	  if(file.exists()) {
	    try {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        StringBuilder sb = new StringBuilder();
        for(Object key:props.keySet()) {
          if(sb.length()>0) {
            sb.append(';');
          }
          sb.append((String)key).append('=').append(props.getProperty((String) key));
        }
        return sb.toString();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        return "";
      } catch (IOException e) {
        e.printStackTrace();
        return "";
      }
	  }
	  return "";
	}
	
	@GET
	@Path("/loconame")
  @Deprecated
	public String getLocoName() {
		return nativeLibrary.GetLocoName();
	}

	@GET
	@Path("/combinedThrottleBrake")
  @Deprecated
	public String isCombinedThrottleBrake() {
		return Boolean.toString(nativeLibrary.GetRailSimCombinedThrottleBrake());
	}
	
}
