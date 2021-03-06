[![Build Status](https://travis-ci.org/reallyinsane/trainsimulator-controller.svg?branch=master)](https://travis-ci.org/reallyinsane/trainsimulator-controller)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6b11f46bd5f34e3ba4c91e96b7ccf99c)](https://www.codacy.com/app/reallyinsane/trainsimulator-controller?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=reallyinsane/trainsimulator-controller&amp;utm_campaign=Badge_Grade)
<a href="https://opensource.org/licenses/Apache-2.0"><img src="https://img.shields.io/badge/license-apache2-blue.svg"></a>

# TrainSimulator Controller

This project was created to use custom controls as input or output while playing TrainSimulator. Especially when activating Sifa and PZB direct interaction is needed as soon as this is indicated by
the game. But not in all situations (e.g. camera position) the regarding controls are visible.  So the idea was, having custom controls outside of the game.
 
 TrainSimulator offers an API to interact with the game and get status of any control provided by the driven locomotive. It is also possible to send control changes to the game, e.g. indicating that
 PZB vigilance has been pressed. So you can use this API to communicate with custom input/output controls.
 
## Project structure

The API provided by TrainSimulator is available by a Windows DLL only and thus can be accessed on the host only. To be able to access the API also from other clients (e.g. a Raspberry PI or Arduino)
a service had to be established. The current project offers a server part with this logic, a client part accessing this service and two sample clients using a Raspberry PI and a FT232h controller.

|module                   |description                                                                            |
|-------------------------|---------------------------------------------------------------------------------------|
|trainsimulator-client    | Client component which can access the REST service provided by trainsimulator-server. |
|trainsimulator-core      | Domain classed used by client and server modules.                                     |
|trainsimulator-ft232h    | Sample client for a FT232h controller used as USB device on the host.                 |
|trainsimulator-parent    | Infrastructure module managing dependencies.                                          |
|trainsimulator-raspberry | Sample client for Raspberry using the service over LAN/WLAN.                          |
|trainsimulator-server    | Server component providing REST service on host.                                      |

## trainsimulator-server

The server module provides a REST service with all functions to interact with TrainSimulator API. It's a Spring Boot application which can be started as fat jar. By default the service will be started 
on port 13913. The REST service provides the following methods:

|method |path                              |description                                                          |
|-------|----------------------------------|---------------------------------------------------------------------|
|GET    |/trainsimulator/locomotive        | Information about the current engine with their supported controls. |
|GET    |/trainsimulator/generic           | Information about the current engine in plain format.               |
|GET    |/trainsimulator/control/{control} | Get value of single control with min, max and current.              |
|PUT    |/trainsimulator/control/{control} | Change value of a single control.                                   |

Using the REST service is one way to interact with TrainSimulator API. Another way is to extend the trainsimulator-server application. For handling changes of a control's value an 
annotated method can be used. Just declare a spring component with a method with io.mathan.trainsimulator.service.Present annotation. This methods accepts a parameter of type
io.mathan.trainsimulator.service.Event and is called each time a control's value changes. (The check is scheduled each 100ms) 

```
@Present
public void present(Event event) {
  Control control = event.getControl();
  ControlData data = event.getData();
}
```
By this you can manage output controls. To manage input controls you have to send events to a trainsimulator-server component. This can be done by having a spring component with the
io.mathan.trainsimulator.service.Collector component injected. If an input control should fire an event the method Collector.raiseEvent(Event) should be used.

```
@Component
public class CustomInputControl {
  private Collector collector;
  public void someMethod() {
    ...
    Control control;
    ControlData data;
    Event event = new Event(control, data);
    collector.raiseEvent(event);
  }
}
``` 

The REST service itself is using both approaches to publish control changes received through the REST call to TrainSimulator and acts as receiver of control changes. These are cached for the REST 
calls.

### Supported controls

There are various controls supported by TrainSimulator. It depends on the selected engine which controls are available. There are also differences in naming of the controls. For a generic API an
defined enum is used to identify the controls.
 In general the API can be used to either display the current/minimum/maximum value of a control and to change the value of a control, e.g. for resetting the VigilAlarm or change the throttle.

The following controls are supported (although there are different names for the controls in different engines, the names chosen are the common used):

- Vigil controls
    - VigilEnable
    - VigilLight
    - VigilAlarm
    - VigilReset
- PZB controls
    - PZBEnable
    - PZB_55
    - PZB_70
    - PZB_85
    - PZB_40
    - PZB_500
    - PZB_1000
    - PzbWarning
    - Cmd_40
    - Cmd_Free
    - Cmd_Wachsam   

### Support for additional locomotives 

The controls above are some of the major controls when using vigil or PZB. The name of the control in TrainSimulator depends on the vendor of the product. It is not the same for all locomotives.
Therefore mappings can be defined if the name of the control does not match the control name above. There are already some default mappings for some of the locomotives I used.

Please see [default.mapping](./trainsimulator-server/src/main/resources/default.mapping) for details.

### Sample trainsimulator-ft232h (second version)

The description of the first version using ft232h can be found in the next chapter. After that I created a second version with more functionality. It is using a Adafruit FT232h breakout as USB decive
too. But this time more devices are connected using I2C protocol. I used:

- 1 x "PZB-Würfel"
- 2 x [MCP23017] for GPIO (GPIOs of [FT232h] cannot be used as I2C is required for other devices)
- 3 x [Sparkfun 4 digit 7 segment] in white, red and blue
- 1 x [Adafruit bargraph] with 24 segments green/yellow/red
- 1 x [LED stripe]
- 2 x On/On Switches (as input)
- 2 x Buzzer

With this sample project I can show all pzb controls, sifa light the LZB distance (<1000m red, <4000m yellow, >4000m green) and numbers/time on the three 4 digit displays. Currently I implemented:
- showing time (scenario time)
- showing LZB distance in meters
- showing LZB speed (Vsoll which is maximum speed allowed in current section, Vziel which is maximum speed allowed in next section)
- showing AFB target speed
- showing current speed

<img src="https://raw.githubusercontent.com/reallyinsane/trainsimulator-controller/master/device.jpg" width="200px">
<img src="https://raw.githubusercontent.com/reallyinsane/trainsimulator-controller/master/device_pzb_1.jpg" width="200px">
<img src="https://raw.githubusercontent.com/reallyinsane/trainsimulator-controller/master/device_pzb_2.jpg" width="200px">
<img src="https://raw.githubusercontent.com/reallyinsane/trainsimulator-controller/master/device_speed.jpg" width="200px">
<img src="https://raw.githubusercontent.com/reallyinsane/trainsimulator-controller/master/device_time.jpg" width="200px">
<img src="https://raw.githubusercontent.com/reallyinsane/trainsimulator-controller/master/device_lzb.jpg" width="200px">


### Sample trainsimulator-ft232h (first version)

This sample project adds a Adafruit FT232h breakout as USB devices to the host and sends the control data using GPIO. Therefore trainsimulator-ft232h extends the Spring Boot application of
trainsimulator-server by registering an additional component to receive events that sent to the FT232h. 

To be able to connect controls (the sample supports output controls only) you have to provide a configuration file `application.properties` in the same folder as the JAR of the spring boot application. 
In this file the mapping is made between the control and the GPIO pin to use. By default the file looks like this:

```
# needed to activate spring component to connect to ft232h
spring.profiles.active=production,pzb
# important for additional clients or when accessing REST service through browser
server.port=13913
pzb.pzb55=C0
pzb.pzb70=C1
pzb.pzb85=C2
pzb.pzb40=C3
pzb.pzb500=C4
pzb.pzb1000=C5
pzb.sifaLight=D1
pzb.sifaWarn=C6
```

This sample supports the following properties:
- `pzb.pzb55`
- `pzb.pzb70`
- `pzb.pzb85`
- `pzb.pzb40`
- `pzb.pzb500`
- `pzb.pzb1000`
- `pzb.sifaLight`
- `pzb.sifaWarn`

As values you can use C0-C7 and D0-D7.


Requirements:
- Hardware:
    - [Adafruit FT232h Breakout](https://www.amazon.de/Adafruit-FT232H-Breakout-General-Purpose/dp/B00XW2MD30)
    - LEDs 
- Software:
    - [D2XX Drivers](https://www.ftdichip.com/Drivers/D2XX.htm)
 
<img src="https://raw.githubusercontent.com/reallyinsane/trainsimulator-controller/master/ft232h_front.jpg" width="200px">
<img src="https://raw.githubusercontent.com/reallyinsane/trainsimulator-controller/master/ft232h_back.jpg" width="200px">

[FT232h]: https://www.amazon.de/Adafruit-FT232H-Breakout-General-Purpose/dp/B00XW2MD30
[MCP23017]: https://www.microchip.com/wwwproducts/en/MCP23017
[Sparkfun 4 digit 7 segment]: https://www.sparkfun.com/products/11441
[Adafruit bargraph]: https://www.adafruit.com/product/1721
[LED stripe]: https://www.amazon.de/gp/product/B071KBY7NW/ref=ox_sc_act_title_2?smid=AQ1IBDB6G2RRD&psc=1
