package de.mathan.trainsimulator.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ControlValue {
  
  private Float current;
  private Float minimum;
  private Float maximum;

  public ControlValue() {
  }
  
  public Float getCurrent() {
    return current;
  }
  
  public Float getMaximum() {
    return maximum;
  }
  
  public Float getMinimum() {
    return minimum;
  }
  
  public void setCurrent(Float current) {
    this.current = current;
  }
  
  public void setMaximum(Float maximum) {
    this.maximum = maximum;
  }
  
  public void setMinimum(Float minimum) {
    this.minimum = minimum;
  }

}
