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

package io.mathan.trainsimulator.model;

public interface Control {

  String SifaEnabled = "VigilEnable";
  String PzbEnabled = "PZBEnable";
  String Pzb55 = "PZB_55";
  String Pzb70 = "PZB_70";
  String Pzb85 = "PZB_85";
  String Pzb40 = "PZB_40";
  String Pzb500 = "PZB_500";
  String Pzb1000 = "PZB_1000";
  String SifaLight = "VigilLight";
  String SifaAlarm = "VigilAlarm";
  String PzbWarning = "PzbWarning";
  String CmdSifa = "VigilReset";
  String CmdPzbWachsam = "Cmd_Wachsam";
  String CmdPzbFrei = "Cmd_Free";
  String CmdPzbBefehl = "Cmd_40";
  String CommonCurrentLatitude = "CommonCurrentLatitude";
  String CommonCurrentLongitude = "CommonCurrentLongitude";
  String CommonFuelLevel = "CommonFuelLevel";
  String CommonTunnel = "CommonTunnel";
  String CommonGradient = "CommonGradient";
  String CommonHeading = "CommonHeading";
  String CommonCurrentTimeHour = "CommonCurrentTimeHour";
  String CommonCurrentTimeMinute = "CommonCurrentTimeMinute";
  String CommonCurrentTimeSecond = "CommonCurrentTimeSecond";
}
