package com.hexaware.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CrimeManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrimeManagementSystemApplication.class, args);
		
		System.out.println(new BCryptPasswordEncoder().encode("user123"));
	}

}
