package de.mathan.trainsimulator.server;

import java.io.Closeable;

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

@Path("/trainsimulator")
public class TrainSimulatorServer {
	static NativeLibrary nativeLibrary = null;

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
	@Path("/loconame")
	public String getLocoName() {
		return nativeLibrary.GetLocoName();
	}

	@GET
	@Path("/combinedThrottleBrake")
	public String isCombinedThrottleBrake() {
		return Boolean.toString(nativeLibrary.GetRailSimCombinedThrottleBrake());
	}

	public static void main(String[] args) throws Exception {
		nativeLibrary = NativeLibraryFactory
				.getInstance("D:\\games\\SteamLibrary\\steamapps\\common\\RailWorks\\plugins\\RailDriver.dll");
		nativeLibrary.SetRailDriverConnected(true);
		nativeLibrary.SetRailSimConnected(true);
		DefaultResourceConfig cfg = new DefaultResourceConfig(TrainSimulatorServer.class);
		cfg.getContainerResponseFilters().add(new GZIPContentEncodingFilter());
		Closeable server = SimpleServerFactory.create("http://localhost:13913", cfg);
		try {
			System.out.println("Press any key to stop the service...");
			System.in.read();
			nativeLibrary.SetRailDriverConnected(false);
			nativeLibrary.SetRailSimConnected(false);
		} finally {
			server.close();
		}
	}

}
