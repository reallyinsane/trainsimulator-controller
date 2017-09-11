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
package de.mathan.trainsimulator.server.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapping {

  private final Map<String, String> simpleMapping = new HashMap<>();
  private final Map<String, List<VirtualMapping>> virtualMapping = new HashMap<>();

  public Mapping() {}

  public Map<String, String> getSimpleMapping() {
    return this.simpleMapping;
  }

  public Map<String, List<VirtualMapping>> getVirtualMapping() {
    return this.virtualMapping;
  }

  public static class VirtualMapping {
    private final String name;
    private final Float value;

    public VirtualMapping(String name, Float value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return this.name;
    }

    public Float getValue() {
      return this.value;
    }
  }
}
