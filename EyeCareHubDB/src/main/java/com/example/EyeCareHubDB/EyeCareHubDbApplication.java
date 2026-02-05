package com.example.EyeCareHubDB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@OpenAPIDefinition(info = @Info(title = "EyeCareHub API", version = "1.0", description = "REST API for EyeCareHub e-commerce platform"))
@SpringBootApplication
public class EyeCareHubDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(EyeCareHubDbApplication.class, args);
	}

}
