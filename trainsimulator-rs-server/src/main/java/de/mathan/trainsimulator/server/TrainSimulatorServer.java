package de.mathan.trainsimulator.server;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import de.mathan.trainsimulator.server.internal.NativeLibrary;
import de.mathan.trainsimulator.server.internal.NativeLibraryFactory;

public class TrainSimulatorServer {
  private static Server server;
	
	/**
	 * Starts the REST-Service on port 13913
	 * @param location The location of the Railworks.dll
	 * @return
	 * @throws Exception
	 */
	public static boolean start(Configuration configuration) throws Exception {
    NativeLibrary nativeLibrary = NativeLibraryFactory
        .getInstance();
    nativeLibrary.SetRailDriverConnected(true);
    nativeLibrary.SetRailSimConnected(true);
    server = configureServer(nativeLibrary, configuration);
    server.start();
//    server.join();
    return true;
	}
	
	/**
	 * Stops the REST-Service
	 * @throws Exception
	 */
	public static void stop() throws Exception {
	  if(server!=null) {
	      NativeLibraryFactory.getInstance().SetRailDriverConnected(false);
	      NativeLibraryFactory.getInstance().SetRailSimConnected(false);
	      server.stop();
	  }
	}
	
  private static Server configureServer(NativeLibrary nativeLibrary, Configuration configuration) {
    ResourceConfig resourceConfig = new ResourceConfig();   
    resourceConfig.packages(TrainSimulatorRS.class.getPackage().getName());
    resourceConfig.register(JacksonFeature.class);
    resourceConfig.register(new AbstractBinder() {
      
      @Override
      protected void configure() {
        bind(nativeLibrary).to(NativeLibrary.class);
      }
    });
    ServletContainer servletContainer = new ServletContainer(resourceConfig);
    ServletHolder sh = new ServletHolder(servletContainer);                
    Server server = new Server(configuration.getRestPort());   
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(sh, "/*");
    server.setHandler(context);
    return server;
  } 	
}
