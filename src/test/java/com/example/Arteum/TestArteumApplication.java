package com.example.Arteum;

import org.springframework.boot.SpringApplication;

import app.Application;

public class TestArteumApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
