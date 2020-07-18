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
  String CmdPzbWachsam = "Cmd_Wachsam"; //CMD_Acknowledge
  String CmdPzbFrei = "Cmd_Free";
  String CmdPzbBefehl = "Cmd_40"; // CMD_Override
  String CommonCurrentLatitude = "CommonCurrentLatitude";
  String CommonCurrentLongitude = "CommonCurrentLongitude";
  String CommonFuelLevel = "CommonFuelLevel";
  String CommonTunnel = "CommonTunnel";
  String CommonGradient = "CommonGradient";
  String CommonHeading = "CommonHeading";
  String CommonCurrentTimeHour = "CommonCurrentTimeHour";
  String CommonCurrentTimeMinute = "CommonCurrentTimeMinute";
  String CommonCurrentTimeSecond = "CommonCurrentTimeSecond";

  String SpeedometerKPH = "SpeedometerKPH";
  String SpeedometerMPH = "SpeedometerMPH"; // for US/UK
  String AWS = "AWS"; // similar to Sifa in England
  String AFBSpeed = "AFB_Speed"; //AFBTargetSpeed
  String LZBSpeed = "LZB_Speed";
  String LZBBuzzer = "LZB_Buzzer";

  String RawTargetDistance = "RawTargetDistance"; // TargetDistance
  String RawSpeedTarget = "RawSpeedTarget";
  /*
  C:\Users\Matthias\ts_temp\DTG\RhineValley1\RailVehicles\Electric\ICE3\Default\Engine\ICE3_driving_A.xml (79 hits)
	Line 1010: 							<ControlName d:type="cDeltaString">TargetSpeed1</ControlName>
	Line 1057: 							<ControlName d:type="cDeltaString">TargetSpeed10</ControlName>
	Line 1185: 							<ControlName d:type="cDeltaString">TargetSpeed100</ControlName>
	Line 1259: 							<ControlName d:type="cDeltaString">AFBSpeed1</ControlName>
	Line 1312: 							<ControlName d:type="cDeltaString">AFBSpeed10</ControlName>
	Line 1428: 							<ControlName d:type="cDeltaString">AFBSpeed100</ControlName>
 C:\Users\Matthias\ts_temp\DTG\RhineValley1\RailVehicles\Electric\BR189\Engine\BR189_Engine_nd.xml (97 hits)
	Line 1286: 							<ControlName d:type="cDeltaString">TargetSpeed100</ControlName>
	Line 1360: 							<ControlName d:type="cDeltaString">TargetSpeed10</ControlName>
	Line 1488: 							<ControlName d:type="cDeltaString">TargetSpeed1</ControlName>
  C:\Users\Matthias\ts_temp\DTG\MunichRosenheim\RailVehicles\Electric\BR101\Default\Engine\br101_nd.xml (78 hits)
	Line 587: 							<ControlName d:type="cDeltaString">LZB_SignalSpeed</ControlName>
	Line 598: 							<ControlName d:type="cDeltaString">LZB_Speed</ControlName>
  BR155_Engine
	Line 3396: 							<ControlName d:type="cDeltaString">LZB_Distance</ControlName>
	Line 3432: 							<ControlName d:type="cDeltaString">LZB_DistanceK</ControlName>
	Line 3522: 							<ControlName d:type="cDeltaString">LZB_DistanceH</ControlName>
	Line 3648: 							<ControlName d:type="cDeltaString">LZB_DistanceBar</ControlName>  *

	Talent2
	Line 6622: 							<ControlName d:type="cDeltaString">Destination</ControlName>
	Line 3785: 							<ControlName d:type="cDeltaString">TargetDistance</ControlName>
	Line 3807: 							<ControlName d:type="cDeltaString">TargetDistanceBar</ControlName>

  * */
}
