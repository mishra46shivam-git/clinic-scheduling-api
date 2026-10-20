package com.clinic.scheduling_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SchedulingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulingApiApplication.class, args);
	}
}
