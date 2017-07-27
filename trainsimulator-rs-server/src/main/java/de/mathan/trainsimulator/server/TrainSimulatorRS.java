package de.mathan.trainsimulator.server;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.Info;
import de.mathan.trainsimulator.model.Mapping;
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
  public Info getInfo() {
    Info ts = new Info();
    ts.setCombindedThrottleBrake(nativeLibrary.GetRailSimCombinedThrottleBrake());
    ts.setLocoName(nativeLibrary.GetLocoName());
    ts.getControls().addAll(getControls());
    return ts;
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
    
  private List<Control> getControls() {
    synchronized (cacheMap) {
      cacheMap.clear();
      List<Control> list = new ArrayList<Control>();
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
          list.add(c);
          cacheMap.put(id, c);
        }   
      }
      return list;
    }
  }
}
