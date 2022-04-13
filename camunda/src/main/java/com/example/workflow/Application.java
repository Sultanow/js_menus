package com.example.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

  public static void main(String... args) {
    SpringApplication.run(Application.class, args);
  }
  
  //Test
  @GetMapping(path = "/api/v1/test")
  public static String get() {
	  return "<h1>Hello</h1><h2>World</h2>";
  }

}