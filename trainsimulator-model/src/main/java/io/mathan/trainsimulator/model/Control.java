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

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum Control {
  SifaEnabled("VigilEnable"),
  PzbEnabled("PZBEnable"),
  Pzb55("PZB_55"),
  Pzb70("PZB_70"),
  Pzb85("PZB_85"),
  Pzb40("PZB_40"),
  Pzb500("PZB_500"),
  Pzb1000("PZB_1000"),
  SifaLight("VigilLight"),
  SifaAlarm("VigilAlarm"),
  PzbWarning("PzbWarning"),
  CmdSifa("VigilReset"),
  CmdPzbWachsam("Cmd_Wachsam"),
  CmdPzbFrei("Cmd_Free"),
  CmdPzbBefehl("Cmd_40");

  private final String value;

  Control(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static Control fromString(String value) {
    for (Control control : Control.values()) {
      if (control.value.equals(value)) {
        return control;
      }
    }
    return null;
  }
}
