package io.mathan.trainsimulator.service;

import io.mathan.trainsimulator.model.ControlData;
import io.mathan.trainsimulator.model.Locomotive;
import io.mathan.trainsimulator.model.generic.GenericLocomotive;

public interface Connector {

  Locomotive getLocomotive() throws TrainSimulatorException;

  ControlData getControlData(String control) throws TrainSimulatorException, UnsupportedControlException;

  void setControlData(String control, ControlData data) throws TrainSimulatorException, UnsupportedControlException;

  GenericLocomotive getGenericLocomotive() throws TrainSimulatorException;
}
