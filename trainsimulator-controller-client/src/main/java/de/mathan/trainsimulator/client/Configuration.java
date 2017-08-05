package de.mathan.trainsimulator.client;

public class Configuration {
  public static final String DEFAULT_MULTICAST_HOST = "224.13.9.13";
  public static final int DEFAULT_MULTICAST_PORT = 13912;
  public static final int DEFAULT_REST_PORT = 13913;
  
  private String multicastHost = DEFAULT_MULTICAST_HOST;
  private int multicastPort = DEFAULT_MULTICAST_PORT;
  private int restPort = DEFAULT_REST_PORT;
  private String restHost;
  
  public String getMulticastHost() {
    return multicastHost;
  }
  
  public int getMulticastPort() {
    return multicastPort;
  }
  
  public String getRestHost() {
    return restHost;
  }
  
  public int getRestPort() {
    return restPort;
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
