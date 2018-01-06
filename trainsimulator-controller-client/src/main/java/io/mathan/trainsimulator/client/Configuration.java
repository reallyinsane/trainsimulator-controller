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
package io.mathan.trainsimulator.client;

public class Configuration {

  public static final String DEFAULT_MULTICAST_HOST = "224.13.9.13";
  public static final int DEFAULT_MULTICAST_PORT = 13912;
  public static final int DEFAULT_REST_PORT = 13913;

  private String multicastHost = DEFAULT_MULTICAST_HOST;
  private int multicastPort = DEFAULT_MULTICAST_PORT;
  private int restPort = DEFAULT_REST_PORT;
  private String restHost;

  public String getMulticastHost() {
    return this.multicastHost;
  }

  public int getMulticastPort() {
    return this.multicastPort;
  }

  public String getRestHost() {
    return this.restHost;
  }

  public int getRestPort() {
    return this.restPort;
  }

  public void setMulticastHost(String multicastHost) {
    this.multicastHost = multicastHost;
  }

  public void setMulticastPort(int multicastPort) {
    this.multicastPort = multicastPort;
  }

  public void setRestHost(String restHost) {
    this.restHost = restHost;
  }

  public void setRestPort(int restPort) {
    this.restPort = restPort;
  }
}
