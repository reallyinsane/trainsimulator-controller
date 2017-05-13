package de.mathan.trainsimulator.client;

public enum Type
  implements ValueEnum<Integer>
{
  Actual(0),  Minimum(1),  Maximum(2);
  
  private final int value;
  
  private Type(int value)
  {
    this.value = value;
  }
  
  public Integer getValue()
  {
    return Integer.valueOf(this.value);
  }
  
  public static Type fromString(int value)
  {
    for (Type type : Type.values()) {
      if (value == type.value) {
        return type;
      }
    }
    return null;
  }
}
