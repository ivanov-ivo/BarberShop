package com.example.barbershop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BarberShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarberShopApplication.class, args);
	}

}
