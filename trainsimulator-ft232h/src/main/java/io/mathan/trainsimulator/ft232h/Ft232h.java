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

import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.FTDIInterface;
import net.sf.yad2xx.mpsse.Mpsse;

/**
 * Class to connect to API provided by yad2xx.
 */
public class Ft232h {

  /**
   * Access GPIO via MPSSE.
   */
  private Mpsse mpsse;
  /**
   * Current state of high data bits.
   */
  private byte high;
  /**
   * Current state of low data bits.
   */
  private byte low;

  static Ft232h getInstance() throws FTDIException {
    System.loadLibrary("FTDIInterface");
    // Get all available FTDI Devices
    Device[] devices = FTDIInterface.getDevices();
    Device dev = devices[0];
    Ft232h access = new Ft232h();
    access.mpsse = new Mpsse(dev);
    try {
      access.mpsse.open();
    } catch (IllegalStateException e) {
      access.mpsse.close();
      access.mpsse.open();
    }
    return access;
  }

  /**
   * Enables a certain pin and updates regarding data bit. {@link #execute()} needs to be called to push the new value to the device.
   */
  public void on(Pin pin) {
    if (pin.isHigh()) {
      high |= pin.getAddress();
    } else {
      low |= pin.getAddress();
    }
  }

  /**
   * Disables a certain pin and updates regarding data bit. {@link #execute()} needs to be called to push the new value to the device.
   */
  public void off(Pin pin) {
    if (pin.isHigh()) {
      high &= ~pin.getAddress();
    } else {
      low &= ~pin.getAddress();
    }
  }

  /**
   * Pushes the data bits to the device.
   */
  public void execute() {
    mpsse.setDataBitsHigh(high, (byte) 0xff);
    mpsse.setDataBitsLow(low, (byte) 0xff);
    mpsse.execute();
  }

  /**
   * Clears all data bits and pushs the changes to the device.
   */
  public void shutdown() {
    mpsse.setDataBitsLow((byte) 0xff, (byte) 0x00);
    mpsse.setDataBitsHigh((byte) 0xff, (byte) 0x00);
    mpsse.execute();
    mpsse.close();
    ;
  }
}