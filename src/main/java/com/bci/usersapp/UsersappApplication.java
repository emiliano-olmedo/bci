package com.bci.usersapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.bci.usersapp")
@EnableJpaRepositories(basePackages = "com.bci.usersapp.repository")
public class UsersappApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersappApplication.class, args);
	}

}
