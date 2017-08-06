package de.mathan.trainsimulator;

import de.mathan.trainsimulator.model.Control;

/**
 * Exception indicating that the requested control is not supported by the active locomotive.
 * @author Matthias
 *
 */
public class UnsupportedControlException extends Exception {

  private static final long serialVersionUID = 1L;
  private Control control;

  public UnsupportedControlException(Control control) {
    super(control.getValue());
    this.control = control;
  }
  
  public Control getControl() {
    return control;
  }

}
