package io.mathan.trainsimulator.service.jni;

import io.mathan.trainsimulator.model.Control;
import io.mathan.trainsimulator.model.ControlData;
import io.mathan.trainsimulator.model.Locomotive;
import io.mathan.trainsimulator.service.Connector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class NativeConnectorTest {

  private static final String LOCO_PRODIVER = "mathan";
  private static final String LOCO_PRODUCT = "test";
  private static final String LOCO_ENGINE = "loco";
  private static final String LOCO_NAME = LOCO_PRODIVER + NativeConnector.DELIMITER_LOCO + LOCO_PRODUCT + NativeConnector.DELIMITER_LOCO + LOCO_ENGINE;
  private static final String CONTROLS_SIMPLE = Control.Pzb55 + NativeConnector.DELIMITER_CONTROLLER + Control.Pzb70;
  private static final String CONTROLS_VIRTUAL = "PZB LM Betrieb";
  private static final String CONTROL_AFB_SPEED_100 = "AFBSpeed100";
  private static final String CONTROL_AFB_SPEED_10 = "AFBSpeed10";
  private static final String CONTROL_AFB_SPEED_1 = "AFBSpeed1";
  private static final String CONTROL_AFB_SPEED = "AFBSpeed";
  private static final String CONTROLS_COMBINED_VALID =
      CONTROL_AFB_SPEED_1 + NativeConnector.DELIMITER_CONTROLLER + CONTROL_AFB_SPEED_10 + NativeConnector.DELIMITER_CONTROLLER + CONTROL_AFB_SPEED_100;
  private static final String CONTROLS_COMBINED_INVALID =
      CONTROL_AFB_SPEED_1 + NativeConnector.DELIMITER_CONTROLLER + CONTROL_AFB_SPEED_10;


  @Autowired
  NativeLibrary nativeLibrary;

  @Autowired
  private Connector connector;

  @TestConfiguration
  public static class Config {

    private NativeLibrary nativeLibrary;

    @Bean
    public NativeLibrary mockedNativeLibrary() {
      if (nativeLibrary == null) {
        nativeLibrary = Mockito.mock(NativeLibrary.class);
      }
      return nativeLibrary;
    }

    @Bean
    @Primary
    public NativeLibraryFactory mockedNativeLibraryFactory() {
      NativeLibraryFactory nativeLibraryFactory = Mockito.mock(NativeLibraryFactory.class);
      Mockito.when(nativeLibraryFactory.getInstance()).thenReturn(mockedNativeLibrary());
      return nativeLibraryFactory;
    }
  }

  @Before
  public void prepare() throws Exception {
    Mockito.when(nativeLibrary.GetLocoName()).thenReturn(LOCO_NAME);

  }

  /**
   * Verifies that locomotive attributes are returned correctly.
   */
  @Test
  public void getLocomotive() throws Exception {
    Locomotive locomotive = connector.getLocomotive();
    Assert.assertEquals(LOCO_PRODIVER, locomotive.getProvider());
    Assert.assertEquals(LOCO_PRODUCT, locomotive.getProduct());
    Assert.assertEquals(LOCO_ENGINE, locomotive.getEngine());
  }

  /**
   * Verifies that simple controls provided by an engine are mapped to their corresponding control and minimum, maximum and current value are collected into a {@link ControlData} object.
   */
  @Test
  public void simpleControls() throws Exception {
    Mockito.when(nativeLibrary.GetControllerList()).thenReturn(CONTROLS_SIMPLE);
    Mockito.when(nativeLibrary.GetControllerValue(0, 0)).thenReturn(1f);
    Mockito.when(nativeLibrary.GetControllerValue(0, 1)).thenReturn(0f);
    Mockito.when(nativeLibrary.GetControllerValue(0, 2)).thenReturn(2f);
    Mockito.when(nativeLibrary.GetControllerValue(1, 0)).thenReturn(2f);
    Mockito.when(nativeLibrary.GetControllerValue(1, 1)).thenReturn(1f);
    Mockito.when(nativeLibrary.GetControllerValue(1, 2)).thenReturn(3f);
    connector.getLocomotive();
    ControlData controlData = connector.getControlData(Control.Pzb55);
    Assert.assertNotNull(controlData);
    Assert.assertEquals(1f, controlData.getCurrent(), 0f);
    Assert.assertEquals(0f, controlData.getMinimum(), 0f);
    Assert.assertEquals(2f, controlData.getMaximum(), 0f);
    controlData = connector.getControlData(Control.Pzb70);
    Assert.assertNotNull(controlData);
    Assert.assertEquals(2f, controlData.getCurrent(), 0f);
    Assert.assertEquals(1f, controlData.getMinimum(), 0f);
    Assert.assertEquals(3f, controlData.getMaximum(), 0f);
  }

  /**
   * Verifies that common controls are provided by an engine even if the engine does not provide own controls.
   */
  @Test
  public void commonControls() throws Exception {
    connector.getLocomotive();
    Mockito.when(nativeLibrary.GetControllerValue(NativeConnector.ID_CommonCurrentTimeHour, 0)).thenReturn(5f);
    Mockito.when(nativeLibrary.GetControllerValue(NativeConnector.ID_CommonCurrentTimeMinute, 0)).thenReturn(6f);
    Mockito.when(nativeLibrary.GetControllerValue(NativeConnector.ID_CommonCurrentTimeSecond, 0)).thenReturn(7f);
    ControlData controlData = connector.getControlData(Control.CommonCurrentTimeHour);
    Assert.assertNotNull(controlData);
    Assert.assertEquals(5f, controlData.getCurrent(), 0f);
    controlData = connector.getControlData(Control.CommonCurrentTimeMinute);
    Assert.assertNotNull(controlData);
    Assert.assertEquals(6f, controlData.getCurrent(), 0f);
    controlData = connector.getControlData(Control.CommonCurrentTimeSecond);
    Assert.assertNotNull(controlData);
    Assert.assertEquals(7f, controlData.getCurrent(), 0f);
  }

  /**
   * Verifies that virtual controls provided by an engine are mapped to the corresponding control in terms of the API.
   */
  @Test
  public void virtualControls() throws Exception {
    Mockito.when(nativeLibrary.GetControllerList()).thenReturn(CONTROLS_VIRTUAL);
    connector.getLocomotive();
//    PZB LM Betrieb=1=PZB_85
//    PZB LM Betrieb=2=PZB_70
//    PZB LM Betrieb=3=PZB_55
    // virtual control is set to 1, which means PZB_85 is active
    Mockito.when(nativeLibrary.GetControllerValue(0, 0)).thenReturn(1f);
    ControlData dataPzb55 = connector.getControlData(Control.Pzb55);
    ControlData dataPzb70 = connector.getControlData(Control.Pzb70);
    ControlData dataPzb85 = connector.getControlData(Control.Pzb85);
    Assert.assertNotNull(dataPzb55);
    Assert.assertNotNull(dataPzb70);
    Assert.assertNotNull(dataPzb85);
    Assert.assertEquals(0f, dataPzb55.getCurrent(), 0f);
    Assert.assertEquals(0f, dataPzb70.getCurrent(), 0f);
    Assert.assertEquals(1f, dataPzb85.getCurrent(), 0f);
    // virtual control is set to 2, which means PZB_70 is active
    Mockito.when(nativeLibrary.GetControllerValue(0, 0)).thenReturn(2f);
    dataPzb55 = connector.getControlData(Control.Pzb55);
    dataPzb70 = connector.getControlData(Control.Pzb70);
    dataPzb85 = connector.getControlData(Control.Pzb85);
    Assert.assertNotNull(dataPzb55);
    Assert.assertNotNull(dataPzb70);
    Assert.assertNotNull(dataPzb85);
    Assert.assertEquals(0f, dataPzb55.getCurrent(), 0f);
    Assert.assertEquals(1f, dataPzb70.getCurrent(), 0f);
    Assert.assertEquals(0f, dataPzb85.getCurrent(), 0f);
    // virtual control is set to 3, which means PZB_55 is active
    Mockito.when(nativeLibrary.GetControllerValue(0, 0)).thenReturn(3f);
    dataPzb55 = connector.getControlData(Control.Pzb55);
    dataPzb70 = connector.getControlData(Control.Pzb70);
    dataPzb85 = connector.getControlData(Control.Pzb85);
    Assert.assertNotNull(dataPzb55);
    Assert.assertNotNull(dataPzb70);
    Assert.assertNotNull(dataPzb85);
    Assert.assertEquals(1f, dataPzb55.getCurrent(), 0f);
    Assert.assertEquals(0f, dataPzb70.getCurrent(), 0f);
    Assert.assertEquals(0f, dataPzb85.getCurrent(), 0f);
  }

  /**
   * Verifies that defined combined controls are available when the engine supports the required single controls.
   */
  @Test
  public void combinedControls() throws Exception {
    Mockito.when(nativeLibrary.GetControllerList()).thenReturn(CONTROLS_COMBINED_VALID);
    connector.getLocomotive();
    Mockito.when(nativeLibrary.GetControllerValue(0, 0)).thenReturn(9f);
    Mockito.when(nativeLibrary.GetControllerValue(1, 0)).thenReturn(5f);
    Mockito.when(nativeLibrary.GetControllerValue(2, 0)).thenReturn(2f);
    ControlData controlData = connector.getControlData(CONTROL_AFB_SPEED);
    Assert.assertNotNull(controlData);
    Assert.assertEquals(259f, controlData.getCurrent(), 0f);
  }

  /**
   * Verifies that defined combined controls are not available when the engine does not support the required single controls.
   */
  @Test
  public void combinedControlsInvalid() throws Exception {
    Mockito.when(nativeLibrary.GetControllerList()).thenReturn(CONTROLS_COMBINED_INVALID);
    connector.getLocomotive();
    ControlData controlData = connector.getControlData(CONTROL_AFB_SPEED);
    Assert.assertNull(controlData);
  }

}
