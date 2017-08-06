package de.mathan.trainsimulator.model.generic;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GenericLocomotive {
  
  private String provider;
  private String product;
  private String engine;
  private boolean combinedThrottleBrake;  
  private List<GenericControl> controls = new ArrayList<>();

  public GenericLocomotive() {
  }
  
  public List<GenericControl> getControls() {
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
  
  public boolean isCombinedThrottleBrake() {
    return combinedThrottleBrake;
  }
  
  public void setCombinedThrottleBrake(boolean combinedThrottleBrake) {
    this.combinedThrottleBrake = combinedThrottleBrake;
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

}
