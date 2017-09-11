/*
 * Copyright 2017 Matthias Hanisch (reallyinsane)
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
package io.mathan.trainsimulator.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import io.mathan.trainsimulator.server.internal.NativeLibrary;
import io.mathan.trainsimulator.server.internal.NativeLibraryFactory;

public class TrainSimulatorServer {
  private static Server server;

  /**
   * Starts the REST-Service on port 13913
   *
   * @param location The location of the Railworks.dll
   * @return
   * @throws Exception
   */
  public static boolean start(Configuration configuration) throws Exception {
    NativeLibrary nativeLibrary = NativeLibraryFactory.getInstance();
    nativeLibrary.SetRailDriverConnected(true);
    nativeLibrary.SetRailSimConnected(true);
    server = configureServer(nativeLibrary, configuration);
    server.start();
    //    server.join();
    return true;
  }

  /**
   * Stops the REST-Service
   *
   * @throws Exception
   */
  public static void stop() throws Exception {
    if (server != null) {
      NativeLibraryFactory.getInstance().SetRailDriverConnected(false);
      NativeLibraryFactory.getInstance().SetRailSimConnected(false);
      server.stop();
    }
  }

  private static Server configureServer(NativeLibrary nativeLibrary, Configuration configuration) {
    ResourceConfig resourceConfig = new ResourceConfig();
    resourceConfig.packages(TrainSimulatorRS.class.getPackage().getName());
    resourceConfig.register(JacksonFeature.class);
    resourceConfig.register(
        new AbstractBinder() {

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
