package de.mathan.trainsimulator.client;

import de.mathan.trainsimulator.client.internal.TrainSimulatorRSClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultTrainSimulator
  implements TrainSimulator
{
  private final TrainSimulatorRSClient client;
  private String currentLocoName = null;
  private Map<Control, Integer> mapApi = new HashMap<Control,Integer>();
  
  public DefaultTrainSimulator(String host, int port)
  {
    this.client = new TrainSimulatorRSClient(host, port);
  }
  
  public String getLocoName()
  {
    de.mathan.trainsimulator.model.Info info = this.client.getInfo();
    String locoName = info.getLocoName();
    if ((locoName != null) && (locoName.length() != 0) && (
      (this.currentLocoName == null) || (!locoName.equals(this.currentLocoName))))
    {
      this.currentLocoName = locoName;
      String currentLocoEngine = locoName.substring(locoName.lastIndexOf(".:.") + 3);
      List<de.mathan.trainsimulator.model.Control> controls = info.getControls();
      Map<String, Integer> mapTSControlToId = new HashMap<String, Integer>();
      for(de.mathan.trainsimulator.model.Control control:controls) {
        mapTSControlToId.put(control.getName(), control.getId());
      }
      this.mapApi = mapTSToApi(mapTSControlToId, currentLocoEngine);
      System.out.println(this.mapApi);
    }
    return locoName;
  }
  
  private void map(String mapping, Map<Control, Integer> mapApi, Map<String, Integer> mapCurrent ) {
    Map<String, String> mapControls = this.client.getMapping(mapping).getEntries();
    for(String keyControl:mapControls.keySet()) {
      String keyApi=mapControls.get(keyControl);
      Integer id = (Integer) mapCurrent.get(keyControl);
      Control control = Control.fromString(keyApi);
      if(id!=null&&control!=null) {
        mapApi.put(control, id);
      }
    }
  }
  
  private Map<Control, Integer> mapTSToApi(Map<String, Integer> mapCurrent, String currentLocoEngine)
  {
    Map<Control, Integer> mapApi = new HashMap<Control,Integer>();
    map("default", mapApi, mapCurrent);
    map(currentLocoEngine, mapApi, mapCurrent);
    
    for (String keyTS : mapCurrent.keySet())
    {
      Control control = Control.fromString(keyTS);
      if (control != null) {
        mapApi.put(control, mapCurrent.get(keyTS));
      }
    }
    return mapApi;
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
    de.mathan.trainsimulator.model.Control c = this.client.getControl(id.intValue());
    switch(type) {
    case Actual:
      return c.getCurrent();
    case Minimum:
      return c.getMinimum();
    case Maximum:
      return c.getMaximum();
    default:
      throw new IllegalArgumentException();
    }
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
      //TODO
      //this.client.setControllerValue(id.intValue(), value);
    }
  }
  
  protected Integer getIdForControl(Control control)
  {
    return (Integer)this.mapApi.get(control);
  }
  
  public boolean has(Control control)
  {
    return getIdForControl(control) != null;
  }
}
