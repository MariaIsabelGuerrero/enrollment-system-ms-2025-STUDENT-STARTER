package com.champlain.enrollmentsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
public class EnrollmentsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnrollmentsServiceApplication.class, args);
	}

}
