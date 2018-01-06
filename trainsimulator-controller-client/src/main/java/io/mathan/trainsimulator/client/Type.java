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
package io.mathan.trainsimulator.client;

public enum Type implements ValueEnum<Integer> {
  Actual(0),
  Minimum(1),
  Maximum(2);

  private final int value;

  private Type(int value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return Integer.valueOf(this.value);
  }

  public static Type fromString(int value) {
    for (Type type : Type.values()) {
      if (value == type.value) {
        return type;
      }
    }
    return null;
  }
}