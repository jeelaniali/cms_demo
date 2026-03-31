package com.hexaware.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.hexaware.cms.model.Role;
import com.hexaware.cms.model.User;
import com.hexaware.cms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CrimeManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrimeManagementSystemApplication.class, args);
	}

	@Bean
	public CommandLineRunner createDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
				User admin = new User();
				admin.setEmail("admin@gmail.com");
				admin.setPassword(passwordEncoder.encode("123456"));
				admin.setName("Admin");
				admin.setRole(Role.STATION_HEAD);
				userRepository.save(admin);
				System.out.println("Default Admin created: admin@gmail.com / 123456");
			}
		};
	}
}
