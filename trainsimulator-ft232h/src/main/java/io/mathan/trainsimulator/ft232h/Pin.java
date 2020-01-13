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
package io.mathan.trainsimulator.ft232h;

/**
 * Enum for the GPIO pin on the FT232h breakout board. Each GPIO pin can be used for one control.
 */
public enum Pin {
  C0((byte) 0x01, true),
  C1((byte) 0x02, true),
  C2((byte) 0x04, true),
  C3((byte) 0x08, true),
  C4((byte) 0x10, true),
  C5((byte) 0x20, true),
  C6((byte) 0x40, true),
  C7((byte) 0x80, true),
  D0((byte) 0x01, false),
  D1((byte) 0x02, false),
  D2((byte) 0x04, false),
  D3((byte) 0x08, false),
  D4((byte) 0x10, false),
  D5((byte) 0x20, false),
  D6((byte) 0x40, false),
  D7((byte) 0x80, false);
  /**
   * Address of the pin.
   */
  private final byte address;
  /**
   * True, if pin is accessed through high data bits or false for low data bits. (All Cx are high and Dx low data bits)
   */
  private final boolean high;

  Pin(byte address, boolean high) {
    this.address = address;
    this.high = high;
  }

  public boolean isHigh() {
    return high;
  }

  public byte getAddress() {
    return address;
  }
}