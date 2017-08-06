package de.mathan.trainsimulator.server.internal;

import java.util.HashMap;
import java.util.Map;

public class SimpleMapping {

  private String name;
  private Map<String, String> entries = new HashMap<>();
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Map<String, String> getEntries() {
    return this.entries;
  }
}
