package de.mathan.trainsimulator.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ControlList {
  public List<Control> controls = new ArrayList<>();
  public ControlList() {
    
  }
  public List<Control> getControls() {
    return controls;
  }
}
