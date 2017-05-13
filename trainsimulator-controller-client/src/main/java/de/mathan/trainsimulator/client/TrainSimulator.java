package de.mathan.trainsimulator.client;

public interface TrainSimulator {
  String getLocoName();

  boolean has(Control paramControl);

  boolean is(Control paramControl);

  Float get(Control paramControl);

  Float get(Control paramControl, Type paramType);

  void press(Control paramControl);

  void set(Control paramControl, boolean paramBoolean);
}
