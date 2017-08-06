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

import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.ControlValue;
import de.mathan.trainsimulator.model.Locomotive;
import de.mathan.trainsimulator.server.internal.NativeLibrary;

@RunWith(MockitoJUnitRunner.class)
public class TrainSimulatorRSTest extends JerseyTest{
  
  private static final Float VALUE_TRUE=1f;
  private static final Float VALUE_FALSE=0f;
  
  private static final int TYPE_MAXIMUM = 2;
  private static final int TYPE_MINIMUM = 1;
  private static final int TYPE_CURRENT = 0;
  private static final Float TEST_VALUE_MAXIMUM = 2f;
  private static final Float TEST_VALUE_MINIMUM = -1f;
  private static final Float TEST_VALUE_CURRENT = 1f;
  private static final String TEST_LOCO = "DTG.:.RhineValley1.:.DB BR189";
  private static final String TEST_LOCO_ENGINE = "DB BR189";
  private static final Integer TEST_CONTROL_1_ID = 0;
  private static final String TEST_CONTROL_1_NAME ="SifaLight";
  private static final String TEST_CONTROL_2_NAME ="VigilAlarm";
  private static final String TEST_CONTROL_LIST = TEST_CONTROL_1_NAME+"::"+TEST_CONTROL_2_NAME;
  
  private static final String TEST_DEFAULT_CONTROL_VIGIL_LIGHT ="VigilLight";
  private static final String TEST_DEFAULT_CONTROL_PZB_500 ="PZB_500";
  private static final String TEST_MAPPED_CONTROL_VIGIL_LIGHT ="SifaLight";
  private static final String TEST_MAPPED_CONTROL_PZB_500 ="PZB_500Hz";
  private static final String TEST_VIRTUAL_CONTROL_PZB ="PZB LM Betrieb";
  
  private static final Float TEST_VIRTUAL_CONTROL_VALUE_PZB_55 =3f;
  private static final Float TEST_VIRTUAL_CONTROL_VALUE_PZB_70 =2f;
  private static final Float TEST_VIRTUAL_CONTROL_VALUE_PZB_85 =1f;
  
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
  }
  
  @Test
  public void getLocomotive() {
    Mockito.when(mock.GetControllerList()).thenReturn(TEST_CONTROL_LIST);
    Response response = target("/trainsimulator/locomotive").request(MediaType.APPLICATION_JSON_TYPE).get();
    Assert.assertEquals(200, response.getStatus());
    Locomotive trainsimulator = response.readEntity(Locomotive.class);
    Assert.assertEquals(TEST_LOCO_ENGINE, trainsimulator.getEngine());
    Assert.assertEquals(Boolean.TRUE, trainsimulator.isCombinedThrottleBrake());
    Assert.assertEquals(2, trainsimulator.getControls().size());
    Assert.assertTrue(trainsimulator.getControls().contains(Control.SifaLight));
    Assert.assertTrue(trainsimulator.getControls().contains(Control.SifaAlarm));
  }
  
  @Test
  public void getController() {
    Mockito.when(mock.GetControllerList()).thenReturn(TEST_CONTROL_LIST);
    Mockito.when(mock.GetControllerValue(TEST_CONTROL_1_ID, TYPE_CURRENT)).thenReturn(TEST_VALUE_CURRENT);
    Mockito.when(mock.GetControllerValue(TEST_CONTROL_1_ID, TYPE_MINIMUM)).thenReturn(TEST_VALUE_MINIMUM);
    Mockito.when(mock.GetControllerValue(TEST_CONTROL_1_ID, TYPE_MAXIMUM)).thenReturn(TEST_VALUE_MAXIMUM);
    Response response = target("/trainsimulator/locomotive").request(MediaType.APPLICATION_JSON_TYPE).get();
    Assert.assertEquals(200, response.getStatus());
    response = target("/trainsimulator/control/"+Control.SifaLight.getValue()).request(MediaType.APPLICATION_JSON).get();
    Assert.assertEquals(200, response.getStatus());
    ControlValue value = response.readEntity(ControlValue.class);
    Assert.assertEquals(TEST_VALUE_CURRENT, value.getCurrent());
    Assert.assertEquals(TEST_VALUE_MINIMUM, value.getMinimum());
    Assert.assertEquals(TEST_VALUE_MAXIMUM, value.getMaximum());
  }

  /**
   * Verifies that default controls are recognized.
   */
  @Test
  public void defaultControls() {
    Mockito.when(mock.GetControllerList()).thenReturn(controls(TEST_DEFAULT_CONTROL_PZB_500, TEST_DEFAULT_CONTROL_VIGIL_LIGHT));
    Response response = target("/trainsimulator/locomotive").request(MediaType.APPLICATION_JSON_TYPE).get();
    Assert.assertEquals(200, response.getStatus());
    Locomotive loco = response.readEntity(Locomotive.class);
    Assert.assertEquals(2, loco.getControls().size());
    Assert.assertTrue(loco.getControls().contains(Control.SifaLight));
    Assert.assertTrue(loco.getControls().contains(Control.Pzb500));
  }
  
  /**
   * Verifies that mapped controls are recognized.
   */
  @Test
  public void mappedControls() {
    Mockito.when(mock.GetControllerList()).thenReturn(controls(TEST_MAPPED_CONTROL_PZB_500, TEST_MAPPED_CONTROL_VIGIL_LIGHT));
    Response response = target("/trainsimulator/locomotive").request(MediaType.APPLICATION_JSON_TYPE).get();
    Assert.assertEquals(200, response.getStatus());
    Locomotive loco = response.readEntity(Locomotive.class);
    Assert.assertEquals(2, loco.getControls().size());
    Assert.assertTrue(loco.getControls().contains(Control.SifaLight));
    Assert.assertTrue(loco.getControls().contains(Control.Pzb500));
  }
  
  /**
   * Verifies that virtual controls are recognizes.
   */
  @Test
  public void virtualControls() {
    Mockito.when(mock.GetControllerList()).thenReturn(controls(TEST_VIRTUAL_CONTROL_PZB));
    Mockito.when(mock.GetControllerValue(TEST_CONTROL_1_ID, TYPE_CURRENT)).thenReturn(TEST_VIRTUAL_CONTROL_VALUE_PZB_55, TEST_VIRTUAL_CONTROL_VALUE_PZB_55, TEST_VIRTUAL_CONTROL_VALUE_PZB_55,
        TEST_VIRTUAL_CONTROL_VALUE_PZB_70, TEST_VIRTUAL_CONTROL_VALUE_PZB_70, TEST_VIRTUAL_CONTROL_VALUE_PZB_70,
        TEST_VIRTUAL_CONTROL_VALUE_PZB_85, TEST_VIRTUAL_CONTROL_VALUE_PZB_85, TEST_VIRTUAL_CONTROL_VALUE_PZB_85);
    Response response = target("/trainsimulator/locomotive").request(MediaType.APPLICATION_JSON_TYPE).get();
    Assert.assertEquals(200, response.getStatus());
    Locomotive loco = response.readEntity(Locomotive.class);
    Assert.assertEquals(3, loco.getControls().size());
    Assert.assertTrue(loco.getControls().contains(Control.Pzb55));
    Assert.assertTrue(loco.getControls().contains(Control.Pzb70));
    Assert.assertTrue(loco.getControls().contains(Control.Pzb85));
    // first three calls should mark PZB55 enabled
    assertControlValue(Control.Pzb55, VALUE_TRUE);
    assertControlValue(Control.Pzb70, VALUE_FALSE);
    assertControlValue(Control.Pzb85, VALUE_FALSE);
    // further three calls should mark PZB70 enabled
    assertControlValue(Control.Pzb55, VALUE_FALSE);
    assertControlValue(Control.Pzb70, VALUE_TRUE);
    assertControlValue(Control.Pzb85, VALUE_FALSE);
    // further three calls should mark PZB85 enabled
    assertControlValue(Control.Pzb55, VALUE_FALSE);
    assertControlValue(Control.Pzb70, VALUE_FALSE);
    assertControlValue(Control.Pzb85, VALUE_TRUE);
  }
  
  private void assertControlValue(Control control, Float expectedValue) {
    Response response = target("/trainsimulator/control/"+control.getValue()).request(MediaType.APPLICATION_JSON).get();
    Assert.assertEquals(200, response.getStatus());
    ControlValue value = response.readEntity(ControlValue.class);
    Assert.assertEquals(expectedValue, value.getCurrent());
  }
  
  private static String controls(String...controls) {
    return String.join("::", controls);
  }
  
}
