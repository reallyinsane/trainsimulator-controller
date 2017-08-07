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
package de.mathan.trainsimulator.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Locomotive {

  private String provider;
  private String product;
  private String engine;
  private final List<Control> controls = new ArrayList<>();
  private boolean combinedThrottleBrake;

  public Locomotive() {}

  public List<Control> getControls() {
    return this.controls;
  }

  public String getEngine() {
    return this.engine;
  }

  public String getProduct() {
    return this.product;
  }

  public String getProvider() {
    return this.provider;
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
    return this.combinedThrottleBrake;
  }

  public void setCombinedThrottleBrake(boolean combinedThrottleBrake) {
    this.combinedThrottleBrake = combinedThrottleBrake;
  }
}
