/*
 * Copyright 2020 Matthias Hanisch (reallyinsane)
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

package io.mathan.trainsimulator.model.generic;

import java.util.ArrayList;
import java.util.List;

public class GenericLocomotive {

  private String provider;
  private String product;
  private String engine;
  private boolean combinedThrottleBrake;
  private final List<GenericControl> controls = new ArrayList<>();

  public List<GenericControl> getControls() {
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

  public boolean isCombinedThrottleBrake() {
    return this.combinedThrottleBrake;
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
