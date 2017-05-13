package de.mathan.trainsimulator.client;

import de.mathan.trainsimulator.client.internal.TrainSimulatorRSClient;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class DefaultTrainSimulator
  implements TrainSimulator
{
  private final TrainSimulatorRSClient client;
  private String currentLocoName = null;
  private Map<Control, Integer> mapApiControlToId = new HashMap();
  
  public DefaultTrainSimulator(String host, int port)
  {
    this.client = new TrainSimulatorRSClient(host, port);
  }
  
  public String getLocoName()
  {
    String locoName = this.client.getLocoName();
    if ((locoName != null) && (locoName.length() != 0) && (
      (this.currentLocoName == null) || (!locoName.equals(this.currentLocoName))))
    {
      this.currentLocoName = locoName;
      String currentLocoEngine = locoName.substring(locoName.lastIndexOf(".:.") + 3);
      Map<String, Integer> mapTSControlToId = this.client.getControllerList();
      this.mapApiControlToId = mapTSToApi(mapTSControlToId, currentLocoEngine);
      System.out.println(this.mapApiControlToId);
    }
    return locoName;
  }
  
  private Map<Control, Integer> mapTSToApi(Map<String, Integer> mapTSControlToId, String currentLocoEngine)
  {
    Map<String, String> mapApiToTSControl = this.client.getMapping(currentLocoEngine);
    Map<Control, Integer> mapApiToId = new HashMap();
    for (String keyApi : mapApiToTSControl.keySet())
    {
      String keyTS = (String)mapApiToTSControl.get(keyApi);
      Integer id = (Integer)mapTSControlToId.get(keyTS);
      Control control = Control.fromString(keyApi);
      if (control != null) {
        mapApiToId.put(control, id);
      }
    }
    for (String keyTS : mapTSControlToId.keySet())
    {
      Control control = Control.fromString(keyTS);
      if (control != null) {
        mapApiToId.put(control, mapTSControlToId.get(keyTS));
      }
    }
    return mapApiToId;
  }
  
  public boolean is(Control control)
  {
    Float value = get(control);
    return Float.valueOf(1.0F).equals(value);
  }
  
  public Float get(Control control)
  {
    return get(control, Type.Actual);
  }
  
  public Float get(Control control, Type type)
  {
    Integer id = getIdForControl(control);
    if (id == null)
    {
      System.out.println(String.format("WARNING: Cannot read value of control %s, control is not available.", new Object[] { control }));
      return null;
    }
    return Float.valueOf(this.client.getControllerValue(id.intValue(), type.getValue().intValue()));
  }
  
  public void press(Control control)
  {
    set(control, true);
    set(control, false);
  }
  
  public void set(Control control, boolean value)
  {
    if (value) {
      set(control, 1.0F);
    } else {
      set(control, 0.0F);
    }
  }
  
  protected void set(Control control, float value)
  {
    Integer id = getIdForControl(control);
    if (id == null) {
      System.out.println(String.format("WARNING: Cannot set value for control %s, control is not available.", new Object[] { control }));
    } else {
      this.client.setControllerValue(id.intValue(), value);
    }
  }
  
  protected Integer getIdForControl(Control control)
  {
    return (Integer)this.mapApiControlToId.get(control);
  }
  
  public boolean has(Control control)
  {
    return getIdForControl(control) != null;
  }
}
