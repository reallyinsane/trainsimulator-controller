package de.mathan.trainsimulator.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Mapping {

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
