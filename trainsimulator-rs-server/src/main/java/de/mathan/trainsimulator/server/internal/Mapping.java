package de.mathan.trainsimulator.server.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapping {
  
  private Map<String, String> simpleMapping = new HashMap<>();
  private Map<String, List<VirtualMapping>> virtualMapping = new HashMap<>();

  public Mapping() {
  }
  
  public Map<String, String> getSimpleMapping() {
    return simpleMapping;
  }
  
  public Map<String, List<VirtualMapping>> getVirtualMapping() {
    return virtualMapping;
  }

  public static class VirtualMapping {
    private final String name;
    private final Float value;
    public VirtualMapping(String name, Float value) {
      this.name = name;
      this.value = value;
    }
    
    public String getName() {
      return name;
    }
    
    public Float getValue() {
      return value;
    }
  }
}
