package de.mathan.trainsimulator.server.internal;

public class VirtualControl {
  final Integer id;
  final Float value;
  public VirtualControl(Integer id, Float value) {
    this.id = id;
    this.value = value;
  }
  
  public Integer getId() {
    return id;
  }
  
  public Float getValue() {
    return value;
  }
}