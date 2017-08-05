package de.mathan.trainsimulator.client;

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
