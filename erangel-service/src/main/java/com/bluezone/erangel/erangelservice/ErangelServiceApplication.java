package com.bluezone.erangel.erangelservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.bluezone.erangel")
public class ErangelServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErangelServiceApplication.class, args);
	}

}
