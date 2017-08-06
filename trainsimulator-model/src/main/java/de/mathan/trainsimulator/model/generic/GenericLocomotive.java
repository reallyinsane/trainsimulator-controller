/*
 * Copyright 2017 Matthias Hanisch (reallyinsane)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
