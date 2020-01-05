package io.mathan.trainsimulator.service;

import io.mathan.trainsimulator.model.Control;
import org.springframework.core.convert.converter.Converter;

public class StringToControlConverter implements Converter<String, Control> {

  @Override
  public Control convert(String s) {
    return Control.fromString(s);
  }
}
