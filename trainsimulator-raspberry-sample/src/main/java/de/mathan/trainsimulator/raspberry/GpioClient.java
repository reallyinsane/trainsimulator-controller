package de.mathan.trainsimulator.raspberry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import de.mathan.trainsimulator.client.Control;
import de.mathan.trainsimulator.client.TrainSimulator;
import de.mathan.trainsimulator.client.TrainSimulatorFactory;

public class GpioClient {
  private static Map<Control, Pin> namePinMapping = new HashMap();
  private static String loco;
  private static GpioController gpio = null;
  
  static
  {
    namePinMapping.put(Control.Pzb85, RaspiPin.GPIO_00);
    namePinMapping.put(Control.Pzb70, RaspiPin.GPIO_01);
    namePinMapping.put(Control.Pzb55, RaspiPin.GPIO_02);
    namePinMapping.put(Control.SifaLight, RaspiPin.GPIO_03);
    namePinMapping.put(Control.Pzb1000, RaspiPin.GPIO_04);
    namePinMapping.put(Control.Pzb500, RaspiPin.GPIO_05);
    namePinMapping.put(Control.Pzb40, RaspiPin.GPIO_06);
  }
  
  public static void main(String[] args)
    throws InterruptedException, IOException
  {
    TrainSimulator api = TrainSimulatorFactory.getInstance();
    final Map<Control, GpioPinDigitalOutput> idOutputMap = new HashMap();
    loco = api.getLocoName();
    System.out.println(String.format("connected to loco %s ", new Object[] { loco }));
    initGpio(api, idOutputMap);
    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      public void run()
      {
        if (GpioClient.gpio != null)
        {
          for (GpioPinDigitalOutput pin : idOutputMap.values()) {
            pin.low();
          }
          GpioClient.gpio.shutdown();
        }
      }
    });
    int run = 1;
    for (;;)
    {
      for (Control control : idOutputMap.keySet())
      {
        GpioPinDigitalOutput pin = (GpioPinDigitalOutput)idOutputMap.get(control);
        if (api.is(control)) {
          pin.high();
        } else {
          pin.low();
        }
      }
      if (run++ % 100 == 0) {
        checkLocoChanged(api, idOutputMap);
      }
      Thread.sleep(10L);
    }
  }
  
  private static void checkLocoChanged(TrainSimulator api, Map<Control, GpioPinDigitalOutput> idOutputMap)
  {
    String newLoco = api.getLocoName();
    if ((newLoco != null) && (!newLoco.equals(loco)))
    {
      loco = newLoco;
      System.out.println("loco changed to " + loco);
      initGpio(api, idOutputMap);
    }
  }
  
  private static void initGpio(TrainSimulator api, Map<Control, GpioPinDigitalOutput> idOutputMap)
  {
    for (GpioPinDigitalOutput pin : idOutputMap.values()) {
      pin.low();
    }
    idOutputMap.clear();
    if (gpio != null)
    {
      for (GpioPin pin : new ArrayList<GpioPin>(gpio.getProvisionedPins())) {
        gpio.unprovisionPin(new GpioPin[] { pin });
      }
      gpio.shutdown();
    }
    gpio = GpioFactory.getInstance();
    for (Control control : namePinMapping.keySet())
    {
      Pin pin = (Pin)namePinMapping.get(control);
      if (api.has(control))
      {
        GpioPinDigitalOutput out = gpio.provisionDigitalOutputPin(pin);
        idOutputMap.put(control, out);
        System.out.println(String.format("initialized control %s on %s", new Object[] { control, pin }));
      }
      else
      {
        System.out.println(String.format("control %s not initialized on %s (no ID for loco)", new Object[] { control, pin }));
      }
    }
  }}
