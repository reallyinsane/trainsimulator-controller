package de.mathan.trainsimulator.model.generic;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GenericControl {
  private Integer id;
  private String name;
  private Float current;
  private Float minimum;
  private Float maximum;

  public GenericControl() {
  }
  
  public Float getCurrent() {
    return current;
  }
  
  public Integer getId() {
    return id;
  }
  
  public Float getMaximum() {
    return maximum;
  }
  
  public Float getMinimum() {
    return minimum;
  }
  
  public String getName() {
    return name;
  }
  
  public void setCurrent(Float current) {
    this.current = current;
  }
  
  public void setId(Integer id) {
    this.id = id;
  }
  
  public void setMaximum(Float maximum) {
    this.maximum = maximum;
  }
  
  public void setMinimum(Float minimum) {
    this.minimum = minimum;
  }
  
  public void setName(String name) {
    this.name = name;
  }

}
