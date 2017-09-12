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
package io.mathan.trainsimulator.server.internal;

public class VirtualControl {
  final Integer id;
  final Float value;

  public VirtualControl(Integer id, Float value) {
    this.id = id;
    this.value = value;
  }

  public Integer getId() {
    return this.id;
  }

  public Float getValue() {
    return this.value;
  }
}
