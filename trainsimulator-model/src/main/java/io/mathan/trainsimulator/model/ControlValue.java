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
package io.mathan.trainsimulator.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ControlValue {

  private Float current;
  private Float minimum;
  private Float maximum;

  public ControlValue() {
  }

  public Float getCurrent() {
    return this.current;
  }

  public Float getMaximum() {
    return this.maximum;
  }

  public Float getMinimum() {
    return this.minimum;
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
