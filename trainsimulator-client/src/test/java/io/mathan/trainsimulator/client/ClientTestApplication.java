package io.mathan.trainsimulator.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"io.mathan.trainsimulator.service", "io.mathan.trainsimulator.client"})
public class ClientTestApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientTestApplication.class, args);
  }
}