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

import java.util.Date;

public class Ft232hRebuild {

  public static final byte ADDRESS_BLUE = 0x72;
  public static final byte ADDRESS_RED = 0x73;
  public static final byte ADDRESS_WHITE = 0x74;
  private I2C device;
  private MCP23017 left;
//  private MCP23017 right;
  private Bargraph bargraph;
  private Display white;
  private Display red;
  private Display blue;


  public static Ft232hRebuild getInstance() throws FTDIException {
    System.loadLibrary("FTDIInterface");
    // Get all available FTDI Devices
    Device[] devices = FTDIInterface.getDevices();
    Ft232hRebuild myDevice = new Ft232hRebuild();
    myDevice.device = new I2C(devices[0]);
    myDevice.device.open();
    myDevice.bargraph = new Bargraph(myDevice.device);
    myDevice.bargraph.clear();
    myDevice.white = new Display(myDevice.device, ADDRESS_WHITE);
    myDevice.red = new Display(myDevice.device, ADDRESS_RED);
    myDevice.blue = new Display(myDevice.device, ADDRESS_BLUE);
    myDevice.white.clear();
    myDevice.red.clear();
    myDevice.blue.clear();

    myDevice.left = new MCP23017(myDevice.device, (byte) 0x20);
    myDevice.device.delay(50);
    myDevice.left.setupOutput(Pin.GPIO_B_0);
    myDevice.left.setupOutput(Pin.GPIO_B_1);
    myDevice.left.setupOutput(Pin.GPIO_B_2);
    myDevice.left.setupOutput(Pin.GPIO_B_3);
    myDevice.left.setupOutput(Pin.GPIO_B_4);
    myDevice.left.setupOutput(Pin.GPIO_B_5);
    myDevice.left.setupOutput(Pin.GPIO_B_6);
    myDevice.left.setupOutput(Pin.GPIO_B_7);
    myDevice.left.setupOutput(Pin.GPIO_A_7);
    myDevice.left.setupInput(Pin.GPIO_A_6);
    myDevice.left.setupInput(Pin.GPIO_A_5);
    myDevice.left.setupInput(Pin.GPIO_A_4);
    myDevice.left.setupInput(Pin.GPIO_A_3);

//    myDevice.right = new MCP23017(myDevice.device, (byte) 0x21);
//    myDevice.device.delay(50);
//    myDevice.right.setupOutput(Pin.GPIO_B_0);
//    myDevice.right.setupOutput(Pin.GPIO_A_3);
//    myDevice.right.setupInput(Pin.GPIO_A_7);
//    myDevice.right.setupInput(Pin.GPIO_A_6);
//    myDevice.right.setupInput(Pin.GPIO_A_5);
//    myDevice.right.setupInput(Pin.GPIO_A_4);

//    myDevice.left.setupOutput(Pin.GPIO_B_1);
//    myDevice.left.setupOutput(Pin.GPIO_B_2);
//    myDevice.left.setupOutput(Pin.GPIO_B_3);
//    myDevice.left.setupOutput(Pin.GPIO_B_4);
//    myDevice.left.setupOutput(Pin.GPIO_B_5);
//    myDevice.left.setupOutput(Pin.GPIO_B_6);
//    myDevice.left.setupOutput(Pin.GPIO_B_7);
//    myDevice.device.delay(50);
//    myDevice.left.off(Pin.GPIO_B_0);
//    myDevice.left.off(Pin.GPIO_B_1);
//    myDevice.left.off(Pin.GPIO_B_2);
//    myDevice.left.off(Pin.GPIO_B_3);
//    myDevice.left.off(Pin.GPIO_B_4);
    return myDevice;
  }

  private static void sleep(long millis) {
      try {
          Thread.sleep(millis);
      } catch (InterruptedException e) {
          throw new RuntimeException(e);
      }
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
    return left.isSet(Pin.GPIO_A_4);
  }

  public boolean isTop2() throws FTDIException {
    return left.isSet(Pin.GPIO_A_3);
  }

  public boolean isFront1() throws FTDIException {
    return left.isSet(Pin.GPIO_A_6);
  }

  public boolean isFront2() throws FTDIException {
    return left.isSet(Pin.GPIO_A_5);
  }

  public void setPzb1000(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_3, enable);
  }

  public void setPzb500(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_2, enable);
  }

  public void setPzb40(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_1, enable);
  }

  public void setPzb85(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_4, enable);
  }

  public void setPzb70(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_5, enable);
  }

  public void setPzb55(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_6, enable);
  }

  public void setLZBBuzzer(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_7, enable);
  }

  public void setSifaBuzzer(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_B_0 , enable);
  }

  public void setSifaLight(boolean enable) throws FTDIException {
    enable(left, Pin.GPIO_A_7, enable);
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

  public static void main(String[] args) throws FTDIException {
    Ft232hRebuild instance = Ft232hRebuild.getInstance();

    // setting all bars to a color
    Color[] colors = {Color.GREEN, Color.RED, Color.YELLOW};
    for(int i=0;i<24;i++) {
      instance.bargraph.setBar((i+1), colors[i%3]);
      instance.bargraph.update();
      instance.device.delay(50);
    }
    // demonstrating percentage usage
    for(int i=0;i<=100;i++) {
      Color color = i>80?Color.RED:i>50?Color.YELLOW:Color.GREEN;
      instance.bargraph.setPercentage((float)i/100, color);
      instance.device.delay(50);
    }
    instance.bargraph.clear();
    int value=0;
    for(int i=0;i<10;i++) {
      Date date = new Date();
//      white.setTime(df1.format(date));
//      red.setTime(df2.format(date));
//      blue.setTime(df1.format(date));
      instance.white.setInt(i);
      instance.device.delay(20);
      instance.red.setInt(9999-i);
      instance.device.delay(20);
      instance.blue.setInt(5000-i);
      instance.device.delay(20);
      value++;
    }
    instance.white.clear();
    instance.red.clear();
    instance.blue.clear();
    instance.setLZBBuzzer(true);
    sleep(500);
    instance.setLZBBuzzer(false);
    sleep(500);
    instance.setSifaBuzzer(true);
    sleep(500);
    instance.setSifaBuzzer(false);
    sleep(500);
    instance.setSifaLight(true);
    sleep(500);
    instance.setSifaLight(false);
    sleep(500);
    instance.setPzb85(true);
    sleep(500);
    instance.setPzb85(false);
    sleep(500);
    instance.setPzb70(true);
    sleep(500);
    instance.setPzb70(false);
    sleep(500);
    instance.setPzb55(true);
    sleep(500);
    instance.setPzb55(false);
    sleep(500);
    instance.setPzb40(true);
    sleep(500);
    instance.setPzb40(false);
    sleep(500);
    instance.setPzb500(true);
    sleep(500);
    instance.setPzb500(false);
    sleep(500);
    instance.setPzb1000(true);
    sleep(500);
    instance.setPzb1000(false);
    sleep(500);
    for(int i=0;i<20;i++) {
      System.out.println("top1= "+instance.isTop1());
      System.out.println("top2= "+instance.isTop2());
      System.out.println("front1= "+instance.isFront1());
      System.out.println("front2= "+instance.isFront2());
      sleep(1000);
    }

    instance.device.close();
  }
}
