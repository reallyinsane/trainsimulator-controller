package io.mathan.trainsimulator.ft232h;

import io.mathan.adafruit.Bargraph;
import io.mathan.adafruit.Bargraph.Color;
import io.mathan.mcp.MCP23017;
import io.mathan.mcp.MCP23017.Pin;
import io.mathan.sparkfun.Display;
import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.FTDIInterface;
import net.sf.yad2xx.mpsse.I2C;

public class Ft232h {

  public static final byte ADDRESS_BLUE = 0x72;
  public static final byte ADDRESS_RED = 0x73;
  public static final byte ADDRESS_WHITE = 0x74;
  private I2C device;
  private MCP23017 left;
  private MCP23017 right;
  private Bargraph bargraph;
  private Display white;
  private Display red;
  private Display blue;


  public static Ft232h getInstance() throws FTDIException {
    System.loadLibrary("FTDIInterface");
    // Get all available FTDI Devices
    Device[] devices = FTDIInterface.getDevices();
    Ft232h myDevice = new Ft232h();
    myDevice.device = new I2C(devices[0]);
    devices[0].open();
//    devices[0].setTimeouts(100, 100);
    myDevice.device.delay(250);
    myDevice.bargraph = new Bargraph(myDevice.device);
    myDevice.bargraph.clear();
    myDevice.left = new MCP23017(myDevice.device, (byte) 0x20);
    myDevice.device.delay(50);
    myDevice.right = new MCP23017(myDevice.device, (byte) 0x21);
    myDevice.device.delay(50);
    myDevice.left.setupOutput(Pin.GPIO_B_0);
    myDevice.left.setupOutput(Pin.GPIO_B_1);
    myDevice.left.setupOutput(Pin.GPIO_B_2);
    myDevice.left.setupOutput(Pin.GPIO_B_3);
    myDevice.left.setupOutput(Pin.GPIO_B_4);
    myDevice.left.setupOutput(Pin.GPIO_B_5);
    myDevice.left.setupOutput(Pin.GPIO_B_6);
    myDevice.left.setupOutput(Pin.GPIO_B_7);
    myDevice.right.setupInput(Pin.GPIO_A_4);
    myDevice.right.setupInput(Pin.GPIO_A_5);
    myDevice.right.setupInput(Pin.GPIO_A_6);
    myDevice.right.setupInput(Pin.GPIO_A_7);
    myDevice.right.setupOutput(Pin.GPIO_B_0);
    myDevice.device.delay(50);
    myDevice.left.off(Pin.GPIO_B_0);
    myDevice.left.off(Pin.GPIO_B_1);
    myDevice.left.off(Pin.GPIO_B_2);
    myDevice.left.off(Pin.GPIO_B_3);
    myDevice.left.off(Pin.GPIO_B_4);
    myDevice.left.off(Pin.GPIO_B_5);
    myDevice.left.update();
    myDevice.white = new Display(myDevice.device, ADDRESS_WHITE);
    myDevice.device.delay(250);
    myDevice.red = new Display(myDevice.device, ADDRESS_RED);
    myDevice.device.delay(250);
    myDevice.blue = new Display(myDevice.device, ADDRESS_BLUE);
    myDevice.device.delay(250);
    myDevice.white.clear();
    myDevice.device.delay(250);
    myDevice.red.clear();
    myDevice.device.delay(250);
    myDevice.blue.clear();
    myDevice.device.delay(250);
    myDevice.white.setBrightness((byte) 100);
    myDevice.device.delay(250);
    myDevice.red.setBrightness((byte) 100);
    myDevice.device.delay(250);
    myDevice.blue.setBrightness((byte) 100);
    myDevice.device.delay(250);
    return myDevice;
  }

  void setWhite(int value) throws FTDIException {
    white.setInt(value);
  }

  void setWhite(float value) throws FTDIException {
    white.setFloat(value, 1);
  }

  void setRed(int value) throws FTDIException {
    device.delay(20);
  }

  void setRed(float value) throws FTDIException {
    red.setFloat(value, 1);
  }

  void setBlue(int value) throws FTDIException {
    blue.setInt(value);
  }

  void setBlue(float value) throws FTDIException {
    blue.setFloat(value, 1);
  }

  public boolean isTop1() throws FTDIException {
    return right.isSet(Pin.GPIO_A_7);
  }

  public boolean isTop2() throws FTDIException {
    return right.isSet(Pin.GPIO_A_6);
  }

  public boolean isFront1() throws FTDIException {
    return right.isSet(Pin.GPIO_A_5);
  }

  public boolean isFront2() throws FTDIException {
    return right.isSet(Pin.GPIO_A_4);
  }

  public void setPzb1000(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_0, enable);
  }

  public void setPzb500(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_1, enable);
  }

  public void setPzb40(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_2, enable);
  }

  public void setPzb85(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_3, enable);
  }

  public void setPzb70(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_4, enable);
  }

  public void setPzb55(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_5, enable);
  }

  public void setLZBBuzzer(boolean enable) throws FTDIException {
    enable(right, Pin.GPIO_B_0, enable);
  }

  public void setSifaBuzzer(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_7, enable);
  }

  public void setSifaLight(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_6, enable);
  }

  private void enable(MCP23017 mcp23017, Pin pin, boolean enable) throws FTDIException {
    if (enable) {
      mcp23017.on(pin);
      mcp23017.update();
    } else {
      mcp23017.off(pin);
      mcp23017.update();
    }
  }

  public void setBar(int value) throws FTDIException {
    bargraph.setPercentage((1f * value) / 100, Color.YELLOW);
  }

  public void setBar(float value, Color color) throws FTDIException {
    bargraph.setPercentage(value, color);
  }

  public void shutdown() {
    this.device.close();
  }

  public void setBlueTime(String time) throws FTDIException {
    this.blue.setTime(time);
  }

  public void setRedTime(String time) throws FTDIException {
    this.red.setTime(time);
  }

  public void setWhiteTime(String time) throws FTDIException {
    this.white.setTime(time);
  }

  public void delay(int i) {
    device.delay(i);
  }
}
