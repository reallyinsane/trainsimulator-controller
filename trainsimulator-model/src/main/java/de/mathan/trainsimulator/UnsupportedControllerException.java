package de.mathan.trainsimulator;

import de.mathan.trainsimulator.model.Controller;

/**
 * Exception indicating that the requested controller is not supported by the active locomotive.
 * @author Matthias
 *
 */
public class UnsupportedControllerException extends Exception {

  private static final long serialVersionUID = 1L;
  private Controller controller;

  public UnsupportedControllerException(Controller controller) {
    super(controller.getValue());
    this.controller = controller;
  }
  
  public Controller getController() {
    return controller;
  }

}
