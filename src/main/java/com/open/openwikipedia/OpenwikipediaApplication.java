package com.open.openwikipedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OpenwikipediaApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenwikipediaApplication.class, args);
	}

}
