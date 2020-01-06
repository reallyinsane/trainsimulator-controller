package io.mathan.trainsimulator.ft232h;

import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.FTDIInterface;
import net.sf.yad2xx.mpsse.Mpsse;

public class Ft232h {

    private Mpsse mpsse;
    private byte high;
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
      } catch(IllegalStateException e) {
        access.mpsse.close();
        access.mpsse.open();
      }
      return access;
    }

    public void on(Pin pin) {
      if (pin.isHigh()) {
        high |= pin.getAddress();
      } else {
        low |= pin.getAddress();
      }
    }

    public void off(Pin pin) {
      if (pin.isHigh()) {
        high &= ~pin.getAddress();
      } else {
        low &= ~pin.getAddress();
      }
    }

    public void execute() {
      mpsse.setDataBitsHigh(high, (byte) 0xff);
      mpsse.setDataBitsLow(low, (byte) 0xff);
      mpsse.execute();
    }

  public void shutdown() {
      mpsse.setDataBitsLow((byte) 0xff, (byte) 0x00);
      mpsse.setDataBitsHigh((byte) 0xff, (byte) 0x00);
      mpsse.execute();
      mpsse.close();;
  }
}