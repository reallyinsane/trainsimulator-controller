package de.mathan.trainsimulator.raspberry;

import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
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

  public static void main(String[] args) throws InterruptedException {
    final TrainSimulator ts = TrainSimulatorFactory.getInstance("192.168.192.19", 13913);
    Map<String, Pin> namePinMapping = new HashMap<String, Pin>();
    namePinMapping.put(PZB_85, RaspiPin.GPIO_00);
    namePinMapping.put(PZB_70, RaspiPin.GPIO_01);
    namePinMapping.put(PZB_55, RaspiPin.GPIO_02);
    namePinMapping.put(SIFA_LIGHT, RaspiPin.GPIO_03);
    namePinMapping.put(PZB_1000, RaspiPin.GPIO_04);
    namePinMapping.put(PZB_500, RaspiPin.GPIO_05);
    namePinMapping.put(PZB_40, RaspiPin.GPIO_06);
    Map<Integer,Pin> idPinMapping = new HashMap<Integer, Pin>();
    for(String name:namePinMapping.keySet()) {
      idPinMapping.put(ts.getControllerList().get(name), namePinMapping.get(name));
    }
    
    final GpioController gpio = GpioFactory.getInstance();
    Map<Integer,GpioPinDigitalOutput> idOutputMap = new HashMap<Integer, GpioPinDigitalOutput>();
    for(Integer id:idPinMapping.keySet()) {
      idOutputMap.put(id,gpio.provisionDigitalOutputPin(idPinMapping.get(id)));
    }
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        gpio.shutdown();
      }
    });
    while(true) {
      for(Integer id:idOutputMap.keySet()) {
        float value =ts.getControllerValue(id, 0);
        if(value==0f) {
          idOutputMap.get(id).low();
        } else {
          idOutputMap.get(id).high();
        }
      }
      Thread.sleep(10);
    }
  }
}
