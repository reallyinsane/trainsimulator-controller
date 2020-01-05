package io.mathan.trainsimulator.ft232h;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pzb")
@Profile("pzb")
public class PzbConfiguration {
  private String pzb55;
  private String pzb70;
  private String pzb85;
  private String pzb40;
  private String pzb500;
  private String pzb1000;
  private String sifaLight;
  private String sifaWarn;

  public String getPzb55() {
    return pzb55;
  }

  public void setPzb55(String pzb55) {
    this.pzb55 = pzb55;
  }

  public String getPzb70() {
    return pzb70;
  }

  public void setPzb70(String pzb70) {
    this.pzb70 = pzb70;
  }

  public String getPzb85() {
    return pzb85;
  }

  public void setPzb85(String pzb85) {
    this.pzb85 = pzb85;
  }

  public String getPzb40() {
    return pzb40;
  }

  public void setPzb40(String pzb40) {
    this.pzb40 = pzb40;
  }

  public String getPzb500() {
    return pzb500;
  }

  public void setPzb500(String pzb500) {
    this.pzb500 = pzb500;
  }

  public String getPzb1000() {
    return pzb1000;
  }

  public void setPzb1000(String pzb1000) {
    this.pzb1000 = pzb1000;
  }

  public String getSifaLight() {
    return sifaLight;
  }

  public void setSifaLight(String sifaLight) {
    this.sifaLight = sifaLight;
  }

  public String getSifaWarn() {
    return sifaWarn;
  }

  public void setSifaWarn(String sifaWarn) {
    this.sifaWarn = sifaWarn;
  }
}
