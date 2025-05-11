package com.example.test_task;

import org.springframework.boot.SpringApplication;

public class TestTestTaskApplication {

	public static void main(String[] args) {
		SpringApplication.from(TestTaskApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
