package de.mathan.trainsimulator.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Control {
  private String name;
  private Integer id;
  private Float current;
  private Float minimum;
  private Float maximum;
  
  public Control() {
    
  }
  
  public Integer getId() {
    return id;
  }
  
  public String getName() {
    return name;
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
  
  public void setId(Integer id) {
    this.id = id;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public void setCurrent(Float current) {
    this.current = current;
    
  }

  public void setMinimum(Float minimum) {
    this.minimum = minimum;
  }

  public void setMaximum(Float maximum) {
    this.maximum = maximum;
  }

  @Override
  public String toString() {
    return String.format("[Control id=%s name=%s cur=%s min=%s max=%s]" , id, name, current, minimum, maximum);
  }
}
