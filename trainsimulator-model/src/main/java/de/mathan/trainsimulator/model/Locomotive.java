package de.mathan.trainsimulator.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Locomotive {
  
  private String provider;
  private String product;
  private String engine;
  private List<Control> controls = new ArrayList<>();
  private boolean combinedThrottleBrake;

  public Locomotive() {
  }
  
  public List<Control> getControls() {
    return controls;
  }
  
  public String getEngine() {
    return engine;
  }
  
  public String getProduct() {
    return product;
  }
  
  public String getProvider() {
    return provider;
  }
  
  public void setEngine(String engine) {
    this.engine = engine;
  }
  
  public void setProduct(String product) {
    this.product = product;
  }
  
  public void setProvider(String provider) {
    this.provider = provider;
  }
  
  public boolean isCombinedThrottleBrake() {
    return combinedThrottleBrake;
  }

  public void setCombinedThrottleBrake(
      boolean combinedThrottleBrake) {
        this.combinedThrottleBrake = combinedThrottleBrake;
  }

}
