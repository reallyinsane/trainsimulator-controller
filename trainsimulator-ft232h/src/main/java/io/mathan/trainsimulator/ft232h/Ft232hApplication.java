package io.mathan.trainsimulator.ft232h;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"io.mathan.trainsimulator.service", "io.mathan.trainsimulator.ft232h"})
public class Ft232hApplication {
  public static void main(String[] args) {
    SpringApplication.run(Ft232hApplication.class, args);
  }
}
