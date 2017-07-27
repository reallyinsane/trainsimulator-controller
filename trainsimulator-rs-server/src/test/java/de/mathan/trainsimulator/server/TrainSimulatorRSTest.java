package de.mathan.trainsimulator.server;

import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.Mapping;
import de.mathan.trainsimulator.model.TrainSimulator;
import de.mathan.trainsimulator.server.internal.NativeLibrary;

@RunWith(MockitoJUnitRunner.class)
public class TrainSimulatorRSTest extends JerseyTest{
  
  private static final int TYPE_MAXIMUM = 2;
  private static final int TYPE_MINIMUM = 1;
  private static final int TYPE_CURRENT = 0;
  private static final Float TEST_VALUE_MAXIMUM = 2f;
  private static final Float TEST_VALUE_MINIMUM = -1f;
  private static final Float TEST_VALUE_CURRENT = 1f;
  private static final String TEST_LOCO = "Loco";
  private static final Integer TEST_CONTROL_1_ID =0;
  private static final Integer TEST_CONTROL_2_ID =1;
  private static final String TEST_CONTROL_1_NAME ="ControlOne";
  private static final String TEST_CONTROL_2_NAME ="ControlTwo";
  private static final String TEST_CONTROLLER_LIST = TEST_CONTROL_1_NAME+"::"+TEST_CONTROL_2_NAME;
  
  @Mock
  private NativeLibrary mock;
  
  @Override
  protected Application configure() {
    ResourceConfig resourceConfig = new ResourceConfig();   
    resourceConfig.packages(TrainSimulatorServer.class.getPackage().getName());
    resourceConfig.register(JacksonFeature.class);
    resourceConfig.register(new AbstractBinder() {
      
      @Override
      protected void configure() {
        bind(mock).to(NativeLibrary.class);
      }
    });
    
    return resourceConfig;
  }
  
  @Before
  public void prepare() {
    Mockito.when(mock.GetLocoName()).thenReturn(TEST_LOCO);
    Mockito.when(mock.GetRailSimCombinedThrottleBrake()).thenReturn(Boolean.TRUE);
    Mockito.when(mock.GetControllerList()).thenReturn(TEST_CONTROLLER_LIST);
  }
  
  
  
  @Test
  public void getInfo() {
    Response response = target("/trainsimulator/info").request(MediaType.APPLICATION_JSON_TYPE).get();
    Assert.assertEquals(200, response.getStatus());
    TrainSimulator trainsimulator = response.readEntity(TrainSimulator.class);
    Assert.assertEquals(TEST_LOCO, trainsimulator.getLocoName());
    Assert.assertEquals(Boolean.TRUE, trainsimulator.isCombindedThrottleBrake());
    Assert.assertEquals(2, trainsimulator.getControls().size());
    assertControl(trainsimulator.getControls(), TEST_CONTROL_1_ID, TEST_CONTROL_1_NAME);
    assertControl(trainsimulator.getControls(), TEST_CONTROL_2_ID, TEST_CONTROL_2_NAME);
  }
  
  @Test
  public void getControl() {
    Mockito.when(mock.GetControllerValue(TEST_CONTROL_1_ID, TYPE_CURRENT)).thenReturn(TEST_VALUE_CURRENT);
    Mockito.when(mock.GetControllerValue(TEST_CONTROL_1_ID, TYPE_MINIMUM)).thenReturn(TEST_VALUE_MINIMUM);
    Mockito.when(mock.GetControllerValue(TEST_CONTROL_1_ID, TYPE_MAXIMUM)).thenReturn(TEST_VALUE_MAXIMUM);
    Response response = target("/trainsimulator/control/"+TEST_CONTROL_1_ID).request(MediaType.APPLICATION_JSON).get();
    Assert.assertEquals(200, response.getStatus());
    Control control = response.readEntity(Control.class);
    Assert.assertEquals(TEST_CONTROL_1_ID, control.getId());
    Assert.assertEquals(TEST_CONTROL_1_NAME, control.getName());
    Assert.assertEquals(TEST_VALUE_CURRENT, control.getCurrent());
    Assert.assertEquals(TEST_VALUE_MINIMUM, control.getMinimum());
    Assert.assertEquals(TEST_VALUE_MAXIMUM, control.getMaximum());
  }
  
  @Test
  public void getDefaultMapping() {
    Response response = target("/trainsimulator/map").queryParam("loco", "default").request(MediaType.APPLICATION_JSON).get();
    Assert.assertEquals(200, response.getStatus());
    Mapping mapping= response.readEntity(Mapping.class);
    assertMapping(mapping, "PZB_B40", "PZB_40");
    assertMapping(mapping, "PZB_500Hz", "PZB_500");
    assertMapping(mapping, "PZB_1000Hz", "PZB_1000");
    assertMapping(mapping, "SifaLight", "VigilLight");
    assertMapping(mapping, "SifaAlarm", "VigilAlarm");
  }
  
  private void assertMapping(Mapping mapping, String specific, String common) {
    Assert.assertTrue(String.format("specific mapping %s for control %s missing", specific, common),  mapping.getEntries().containsKey(specific));
    Assert.assertEquals(String.format("specific mapping %s for control %s is wrong and points to control %s",specific, common, mapping.getEntries().get(specific)), common, mapping.getEntries().get(specific));
  }

  private void assertControl(List<Control> controls, int id, String name) {
    for(Control control:controls) {
      if(control.getId().intValue()==id&&name.equals(control.getName())) {
        return;
      }
    }
    Assert.fail(String.format("No control with id=%s and name=%s found. But found %s",  id, name, controls));
  }
}
