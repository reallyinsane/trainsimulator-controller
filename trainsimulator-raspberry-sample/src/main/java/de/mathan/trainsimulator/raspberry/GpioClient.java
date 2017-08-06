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

import de.mathan.trainsimulator.TrainSimulatorException;
import de.mathan.trainsimulator.UnsupportedControllerException;
import de.mathan.trainsimulator.client.TrainSimulatorClient;
import de.mathan.trainsimulator.client.TrainSimulatorClientFactory;
import de.mathan.trainsimulator.model.Controller;

public class GpioClient {
  private static Map<Controller, Pin> namePinMapping = new HashMap<Controller, Pin>();
  private static String loco;
  private static GpioController gpio = null;

  static {
    namePinMapping.put(Controller.Pzb85, RaspiPin.GPIO_00);
    namePinMapping.put(Controller.Pzb70, RaspiPin.GPIO_01);
    namePinMapping.put(Controller.Pzb55, RaspiPin.GPIO_02);
    namePinMapping.put(Controller.SifaLight, RaspiPin.GPIO_03);
    namePinMapping.put(Controller.Pzb1000, RaspiPin.GPIO_04);
    namePinMapping.put(Controller.Pzb500, RaspiPin.GPIO_05);
    namePinMapping.put(Controller.Pzb40, RaspiPin.GPIO_06);
  }

  public static void main(String[] args)
      throws InterruptedException, IOException {
    TrainSimulatorClient api = TrainSimulatorClientFactory.getInstance();
    final Map<Controller, GpioPinDigitalOutput> idOutputMap = new HashMap<Controller, GpioPinDigitalOutput>();
    checkLocoChanged(api, idOutputMap);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        if (GpioClient.gpio != null) {
          for (GpioPinDigitalOutput pin : idOutputMap.values()) {
            pin.low();
          }
          GpioClient.gpio.shutdown();
        }
      }
    });
    int run = 1;
    while (true) {
      for (Controller controller : idOutputMap.keySet()) {
        GpioPinDigitalOutput pin = (GpioPinDigitalOutput) idOutputMap
            .get(controller);
        try {
          if (api.is(controller)) {
            pin.high();
          } else {
            pin.low();
          }
        } catch (UnsupportedControllerException e) {
          pin.low();
        } catch (TrainSimulatorException e) {
          pin.low();
        }
      }
      if (run++ % 100 == 0) {
        checkLocoChanged(api, idOutputMap);
      }
      Thread.sleep(10L);
    }
  }

  private static void checkLocoChanged(TrainSimulatorClient api,
      Map<Controller, GpioPinDigitalOutput> idOutputMap) {
    try {
      String newLoco = api.getLocoName();
      if ((newLoco != null) && (!newLoco.equals(loco))) {
        loco = newLoco;
        System.out.println("loco changed to " + loco);
        initGpio(api, idOutputMap);
      }
    } catch (TrainSimulatorException e) {
      System.out.println("failed to connect to server");
    }
  }

  private static void initGpio(TrainSimulatorClient api,
      Map<Controller, GpioPinDigitalOutput> idOutputMap) {
    for (GpioPinDigitalOutput pin : idOutputMap.values()) {
      pin.low();
    }
    idOutputMap.clear();
    if (gpio != null) {
      for (GpioPin pin : new ArrayList<GpioPin>(gpio.getProvisionedPins())) {
        gpio.unprovisionPin(new GpioPin[] { pin });
      }
      gpio.shutdown();
    }
    gpio = GpioFactory.getInstance();
    for (Controller controller : namePinMapping.keySet()) {
      Pin pin = (Pin) namePinMapping.get(controller);
      if (api.has(controller)) {
        GpioPinDigitalOutput out = gpio.provisionDigitalOutputPin(pin);
        idOutputMap.put(controller, out);
        System.out.println(String.format("initialized Controller %s on %s",
            new Object[] { controller, pin }));
      } else {
        System.out.println(
            String.format("Controller %s not initialized on %s (no ID for loco)",
                new Object[] { controller, pin }));
      }
    }
  }
}
