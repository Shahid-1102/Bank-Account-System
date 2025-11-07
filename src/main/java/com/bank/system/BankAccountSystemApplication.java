package com.bank.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BankAccountSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankAccountSystemApplication.class, args);
	}

}
