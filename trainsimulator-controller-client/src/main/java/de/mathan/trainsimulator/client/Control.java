package de.mathan.trainsimulator.client;

public enum Control
  implements ValueEnum<String>
{
  SifaEnabled("VigilEnable"),//
  PzbEnabled("PZBEnable"),//
  Pzb55("PZB_55"),//
  Pzb70("PZB_70"),//
  Pzb85("PZB_85"),//
  Pzb40("PZB_40"),//
  Pzb500("PZB_500"),//
  Pzb1000("PZB_1000"),//
  SifaLight("VigilLight"),//
  SifaAlarm("VigilAlarm"),//
  PzbWarning("PzbWarning"),//
  CmdSifa("VigilReset"),//
  CmdPzbWachsam("Cmd_Wachsam"),//
  CmdPzbFrei("Cmd_Free"),//
  CmdPzbBefehl("Cmd_40");
  
  private final String value;
  
  private Control(String value)
  {
    this.value = value;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public static Control fromString(String value)
  {
    for (Control control : Control.values()) {
      if (control.value.equals(value)) {
        return control;
      }
    }
    return null;
  }
}
