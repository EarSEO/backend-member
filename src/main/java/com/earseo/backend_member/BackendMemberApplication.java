package com.earseo.backend_member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackendMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendMemberApplication.class, args);
	}

}
