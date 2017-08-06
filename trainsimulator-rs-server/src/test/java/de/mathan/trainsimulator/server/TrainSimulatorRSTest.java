package de.mathan.trainsimulator.server;

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

import de.mathan.trainsimulator.model.Controller;
import de.mathan.trainsimulator.model.ControllerValue;
import de.mathan.trainsimulator.model.Locomotive;
import de.mathan.trainsimulator.server.internal.NativeLibrary;

@RunWith(MockitoJUnitRunner.class)
public class TrainSimulatorRSTest extends JerseyTest{
  
  private static final int TYPE_MAXIMUM = 2;
  private static final int TYPE_MINIMUM = 1;
  private static final int TYPE_CURRENT = 0;
  private static final Float TEST_VALUE_MAXIMUM = 2f;
  private static final Float TEST_VALUE_MINIMUM = -1f;
  private static final Float TEST_VALUE_CURRENT = 1f;
  private static final String TEST_LOCO = "DTG.:.RhineValley1.:.DB BR189";
  private static final String TEST_LOCO_ENGINE = "DB BR189";
  private static final Integer TEST_CONTROL_1_ID = 0;
  private static final Integer TEST_CONTROL_2_ID = 1;
  private static final String TEST_CONTROL_1_NAME ="SifaLight";
  private static final String TEST_CONTROL_2_NAME ="VigilAlarm";
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
  public void getLocomotive() {
    Response response = target("/trainsimulator/locomotive").request(MediaType.APPLICATION_JSON_TYPE).get();
    Assert.assertEquals(200, response.getStatus());
    Locomotive trainsimulator = response.readEntity(Locomotive.class);
    Assert.assertEquals(TEST_LOCO_ENGINE, trainsimulator.getEngine());
    Assert.assertEquals(Boolean.TRUE, trainsimulator.isCombinedThrottleBrake());
    Assert.assertEquals(2, trainsimulator.getController().size());
    Assert.assertTrue(trainsimulator.getController().contains(Controller.SifaLight));
    Assert.assertTrue(trainsimulator.getController().contains(Controller.SifaAlarm));
  }
  
  @Test
  public void getController() {
    Mockito.when(mock.GetControllerValue(TEST_CONTROL_1_ID, TYPE_CURRENT)).thenReturn(TEST_VALUE_CURRENT);
    Mockito.when(mock.GetControllerValue(TEST_CONTROL_1_ID, TYPE_MINIMUM)).thenReturn(TEST_VALUE_MINIMUM);
    Mockito.when(mock.GetControllerValue(TEST_CONTROL_1_ID, TYPE_MAXIMUM)).thenReturn(TEST_VALUE_MAXIMUM);
    Response response = target("/trainsimulator/locomotive").request(MediaType.APPLICATION_JSON_TYPE).get();
    Assert.assertEquals(200, response.getStatus());
    response = target("/trainsimulator/controller/"+Controller.SifaLight.getValue()).request(MediaType.APPLICATION_JSON).get();
    Assert.assertEquals(200, response.getStatus());
    ControllerValue value = response.readEntity(ControllerValue.class);
    Assert.assertEquals(TEST_VALUE_CURRENT, value.getCurrent());
    Assert.assertEquals(TEST_VALUE_MINIMUM, value.getMinimum());
    Assert.assertEquals(TEST_VALUE_MAXIMUM, value.getMaximum());
  }
  
}
