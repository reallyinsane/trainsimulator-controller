package de.mathan.trainsimulator.client;

public class TrainSimulatorTest {

  public static void main(String[] args) {
    TrainSimulator ts = new DefaultTrainSimulator("localhost", 13913);
    ts.getLocoName();
    ts.get(Control.SifaLight);
  }
}
