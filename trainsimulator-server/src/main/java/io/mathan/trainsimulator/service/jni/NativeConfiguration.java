package io.mathan.trainsimulator.service.jni;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "native")
public class NativeConfiguration {

  private String dllLocation;

  public void setDllLocation(String dllLocation) {
    this.dllLocation = dllLocation;
  }

  public String getDllLocation() {
    return dllLocation;
  }
}
