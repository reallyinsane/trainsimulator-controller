package de.mathan.trainsimulator.raspberry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import de.mathan.trainsimulator.client.TrainSimulator;
import de.mathan.trainsimulator.client.TrainSimulatorFactory;

public class GpioClient {
  public static final String SIFA_LIGHT ="VigilLight";
  public static final String SIFA_ALARM="VigilAlarm";
  public static final String SIFA_RESET="VigilReset";
  public static final String PZB_1000="PZB_1000";
  public static final String PZB_500="PZB_500";
  public static final String PZB_40="PZB_40";
  public static final String PZB_55="PZB_55";
  public static final String PZB_70="PZB_70";

  public static final String PZB_85="PZB_85";

  private static Map<String, Pin> namePinMapping = new HashMap<String, Pin>();
  private static String loco;

  private static GpioController gpio =null;

  static {
    namePinMapping.put(PZB_85, RaspiPin.GPIO_00);
    namePinMapping.put(PZB_70, RaspiPin.GPIO_01);
    namePinMapping.put(PZB_55, RaspiPin.GPIO_02);
    namePinMapping.put(SIFA_LIGHT, RaspiPin.GPIO_03);
    namePinMapping.put(PZB_1000, RaspiPin.GPIO_04);
    namePinMapping.put(PZB_500, RaspiPin.GPIO_05);
    namePinMapping.put(PZB_40, RaspiPin.GPIO_06);
  }

  public static void main(String[] args) throws InterruptedException {
    final TrainSimulator ts = TrainSimulatorFactory.getInstance("192.168.192.19", 13913);
    final Map<Integer,GpioPinDigitalOutput> idOutputMap = new HashMap<Integer, GpioPinDigitalOutput>();
    loco =ts.getLocoName();
    System.out.println(String.format("connected to loco %s ", loco));
    initGpio(ts, idOutputMap);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        if(gpio!=null) {
          for(GpioPinDigitalOutput pin:idOutputMap.values()) {
            pin.low();
          }
              
          gpio.shutdown();
        }
      }
    });
    int run=1;
    while(true) {
      for(Integer id:idOutputMap.keySet()) {
        float value =ts.getControllerValue(id, 0);
        if(value==0f) {
          idOutputMap.get(id).low();
        } else {
          idOutputMap.get(id).high();
        }
      }
      if(run++%100==0) {
        checkLocoChanged(ts,  idOutputMap);
      }
      Thread.sleep(10);
    }
  }

  private static void checkLocoChanged(TrainSimulator ts,  Map<Integer,GpioPinDigitalOutput> idOutputMap) {
    String newLoco = ts.getLocoName();
    if(newLoco!=null&&!newLoco.equals(loco)) {
      loco=newLoco;
      System.out.println("loco changed to "+loco);
      initGpio(ts, idOutputMap);
    }
  }

  private static void initGpio(TrainSimulator ts, 
      Map<Integer, GpioPinDigitalOutput> idOutputMap) {
    for(GpioPinDigitalOutput pin:idOutputMap.values()) {
      pin.low();
    }
    idOutputMap.clear();
    if(gpio!=null) {
      for(GpioPin pin:new ArrayList<GpioPin>(gpio.getProvisionedPins())) {
        gpio.unprovisionPin(pin);
      }
      gpio.shutdown();
    }
    gpio = GpioFactory.getInstance();
    Map<Integer,Pin> idPinMapping = new HashMap<Integer, Pin>();
    for(String name:namePinMapping.keySet()) {
      Integer id = ts.getControllerList().get(name);
      Pin pin = namePinMapping.get(name);
      if(id!=null) {
        idPinMapping.put(id, pin);
        System.out.println(String.format("initialized control %s with ID %s on Gpio %s", name,id, pin));
      } else {
        System.out.println(String.format("control %s not initialized on Gpio %s (no ID for loco)", name,id, pin));
      }
    }

    for(Integer id:idPinMapping.keySet()) {
      GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(idPinMapping.get(id));
      pin.low();
      idOutputMap.put(id,pin);
    }
  }
}
