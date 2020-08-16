package io.mathan.trainsimulator.service.jni;

import java.util.List;

public class CombinedControl {

  private final List<Integer> ids;

  public CombinedControl(List<Integer> ids) {

    this.ids = ids;
  }

  public List<Integer> getIds() {
    return ids;
  }
}
