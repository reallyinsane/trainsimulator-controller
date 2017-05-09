# trainsimulator-controller
Client and server components for exchange of controller information and commands between train simulator and devices.

TrainSimulator offers an API to interact with the controls of a loco. Unfortunately the access is limited to local access using an Windows DLL. The purpose of this project is to publish the API as a REST service and providing a Java client API to access the REST service. The client API can then be used to send events from hardware controls (e.g. from Raspberry) via the REST service to TrainSimulator and display control information (like PZB/Sifa) with own LEDs.

## trainsimulator-controller-server

The server component is located on the Windows PC where TrainSimulator is running and connects to the DLL. To start the server run the JAR with a 32bit JRE like this:

```
javaw -jar trainsimulator-controller-server-0.0.1-SNAPSHOT.jar
```
After start a new systray icon appears. If the REST service was successfully started you should see the following icon :![Logo running](/trainsimulator-controller-server/src/main/resources/railway-station-green.png). If the TrainSimulator installation could not be found the icon remains blue :: ![Logo not running](/trainsimulator-controller-server/src/main/resources/railway-station-blue.png). If so check the context menu and specify the absolute path of the Railworks.dll. After this you can start the server via the context menu manually.

## trainsimulator-controller-client

The client component provides a more readable API to access certain controls. Therfore it can connect to the running server.
