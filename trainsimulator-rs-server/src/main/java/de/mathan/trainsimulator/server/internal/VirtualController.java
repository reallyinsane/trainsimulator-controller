package de.mathan.trainsimulator.server.internal;

public class VirtualController {
  final Integer id;
  final Float value;
  public VirtualController(Integer id, Float value) {
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