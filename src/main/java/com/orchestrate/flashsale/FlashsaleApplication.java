package com.orchestrate.flashsale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.orchestrate.flashsale.entities")
public class FlashsaleApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlashsaleApplication.class, args);
	}

}
