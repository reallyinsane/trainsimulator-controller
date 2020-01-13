package io.mathan.trainsimulator.service;

import io.mathan.trainsimulator.model.Control;
import io.mathan.trainsimulator.model.ControlData;
import io.mathan.trainsimulator.model.Locomotive;
import io.mathan.trainsimulator.model.generic.GenericLocomotive;

public interface Connector {

  Locomotive getLocomotive() throws TrainSimulatorException;

  ControlData getControlData(Control control) throws TrainSimulatorException, UnsupportedControlException;

  void setControlData(Control control, ControlData data) throws TrainSimulatorException, UnsupportedControlException;

  GenericLocomotive getGenericLocomotive() throws TrainSimulatorException;
}
