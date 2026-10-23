package com.alexa.healthassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 
public class HealthassistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthassistantApplication.class, args);
	}

}
