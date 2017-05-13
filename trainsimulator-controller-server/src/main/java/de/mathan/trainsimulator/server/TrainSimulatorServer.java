package de.mathan.trainsimulator.server;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.simple.container.SimpleServerFactory;

import de.mathan.trainsimulator.server.internal.NativeLibrary;
import de.mathan.trainsimulator.server.internal.NativeLibraryFactory;

/**
 * REST-Service delegating the requests to Railworks.dll using JNA. 
 * @author Matthias Hanisch
 */
@Path("/trainsimulator")
public class TrainSimulatorServer {
	static NativeLibrary nativeLibrary = null;
  private static Closeable server;

	@GET
	@Path("/list")
	public String getControllerList() {
		return nativeLibrary.GetControllerList();
	}

	@GET
	@Path("/controller/{controllerId}")
	public String getControllerValue(@PathParam("controllerId") int controller, @QueryParam("type") int type) {
		return String.valueOf(nativeLibrary.GetControllerValue(controller, type));
	}

	@PUT
	@Path("/controller/{controllerId}")
	public void setControllerValue(@PathParam("controllerId") int controller, @QueryParam("value") float value) {
		nativeLibrary.SetControllerValue(controller, value);
	}
	
	@GET
	@Path("/mapping")
	public String getMapping(@QueryParam("loco") String loco) {
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
	public String getLocoName() {
		return nativeLibrary.GetLocoName();
	}

	@GET
	@Path("/combinedThrottleBrake")
	public String isCombinedThrottleBrake() {
		return Boolean.toString(nativeLibrary.GetRailSimCombinedThrottleBrake());
	}
	
	/**
	 * Starts the REST-Service on port 13913
	 * @param location The location of the Railworks.dll
	 * @return
	 * @throws Exception
	 */
	public static boolean start() throws Exception {
    nativeLibrary = NativeLibraryFactory
        .getInstance();
    nativeLibrary.SetRailDriverConnected(true);
    nativeLibrary.SetRailSimConnected(true);
    DefaultResourceConfig cfg = new DefaultResourceConfig(TrainSimulatorServer.class);
    cfg.getContainerResponseFilters().add(new GZIPContentEncodingFilter());
    server = SimpleServerFactory.create("http://localhost:13913", cfg);
    return true;
	}
	
	/**
	 * Stops the REST-Service
	 * @throws Exception
	 */
	public static void stop() throws Exception {
	  if(server!=null) {
	      nativeLibrary.SetRailDriverConnected(false);
	      nativeLibrary.SetRailSimConnected(false);
	      server.close();
	  }
	}
}
