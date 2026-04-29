package com.example.lojix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class LojixApplication {

	public static void main(String[] args) {
		SpringApplication.run(LojixApplication.class, args);
	}

}
