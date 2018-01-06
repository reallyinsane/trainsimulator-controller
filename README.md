[![Build Status](https://travis-ci.org/reallyinsane/mathan-latex-maven-plugin.svg?branch=master)](https://travis-ci.org/reallyinsane/trainsimulator-controller)
<a href="https://opensource.org/licenses/Apache-2.0"><img src="https://img.shields.io/badge/license-apache2-blue.svg"></a>

# trainsimulator-controller
Client and server components for exchange of controller information and commands between train simulator and devices.

TrainSimulator offers an API to interact with the controls of a loco. Unfortunately the access is limited to local access using an Windows DLL. The purpose of this project is to publish the API as a REST service and providing a Java client API to access the REST service. The client API can then be used to send events from hardware controls (e.g. from Raspberry) via the REST service to TrainSimulator and display control information (like PZB/Sifa) with own LEDs.

## trainsimulator-rs-server

The server component is located on the Windows PC where TrainSimulator is running and connects to the DLL. To start the server run the JAR with a 32bit JRE like this:

```
javaw -jar trainsimulator-rs-server-0.0.1-SNAPSHOT.jar 
```
After start a new systray icon appears. If the REST service was successfully started you should see the following icon :![Logo running](/trainsimulator-rs-server/src/main/resources/railway-station-green.png). If the TrainSimulator installation could not be found the icon remains blue :: ![Logo not running](/trainsimulator-rs-server/src/main/resources/railway-station-blue.png). If so check the context menu and specify the absolute path of the Railworks.dll. After this you can start the server via the context menu manually.

## trainsimulator-controller-client

The client component provides a more readable API to access certain controls. Therefore it can connect to the running server.

## Supported controls

There are various controls supported by TrainSimulator. It depends on the selected engine which controls are available. There are also differences in naming of the controls. For a generic API an defined enum is used to identify the controls.
 In general the API can be used to either display the current/minimum/maximum value of a control and to change the value of a control, e.g. for reseting the VigilAlarm or change the throttle.

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

### Mappings

For the trainsimulator-rs-server mappings can be defined if control names of an engine differ from the API names. In the file `default.mapping` the mapping can be defined like this:

    # Lines beginning with a hash sign are treated as comments.
    # <loco control name>=<API control name>
    #
    # Samples
    PZB_B40=PZB_40
    PZB_500Hz=PZB_500
    PZB_1000Hz=PZB_1000    

#### Virtual controls

In some cases the engine combine multiple controls to a single one. Different values for such a virtual control represents a &quot;real&quot; control then. Such a mapping can be defined in `default.mapping` like this:

    # <loco control name>=<value>=<API control name>
    #
    # Samples
    PZB LM Betrieb=1=PZB_85
    PZB LM Betrieb=2=PZB_70
    PZB LM Betrieb=3=PZB_55

In the sample above the engine offers the control &quot;PZB LM Betrieb&quot; but not the API controls &quot;PZB_55&quot;, &quot;PZB_70&quot; and &quot;PZB_85&quot;. But each of the API contros can be mapped by the defined value for the loco control.
