package de.mathan.trainsimulator;

import de.mathan.trainsimulator.model.Control;
import de.mathan.trainsimulator.model.Mapping;
import de.mathan.trainsimulator.model.TrainSimulator;

public interface TrainSimulatorService {

  TrainSimulator getInfo();
  Mapping getMapping(String loco);
  Control getControl(int controllerId);
}
