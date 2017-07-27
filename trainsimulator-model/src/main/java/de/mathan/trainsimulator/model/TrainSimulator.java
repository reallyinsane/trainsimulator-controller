package de.mathan.trainsimulator.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TrainSimulator {

  private String locoName;
  private boolean combindedThrottleBrake;
  private List<Control> controls = new ArrayList<Control>();
  
  public List<Control> getControls() {
    return controls;
  }
  
  public String getLocoName() {
    return locoName;
  }
  
  public boolean isCombindedThrottleBrake() {
    return combindedThrottleBrake;
  }
  
  public void setCombindedThrottleBrake(boolean combindedThrottleBrake) {
    this.combindedThrottleBrake = combindedThrottleBrake;
  }
  
  public void setLocoName(String locoName) {
    this.locoName = locoName;
  }
}
