package de.mathan.trainsimulator;

/**
 * Exception indicating that the request to the trainsimulator-server was not successful.
 * @author Matthias
 *
 */
public class TrainSimulatorException extends Exception {

  private static final long serialVersionUID = 1L;

  public TrainSimulatorException(String message) {
    super(message);
  }

  public TrainSimulatorException(String message, Throwable cause) {
    super(message, cause);
  }

}
