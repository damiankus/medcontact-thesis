package com.medcontact.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.medcontact.data.model.Administrator;
import com.medcontact.data.model.Sex;
import com.medcontact.data.repository.AdministratorRepository;

@SpringBootApplication
@ComponentScan(basePackages={
		"com.medcontact.controller", 
		"com.medcontact.security.config", 
		"com.medcontact.data.config"
})
public class Application implements CommandLineRunner {

	@Autowired
	AdministratorRepository adminRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Administrator admin = new Administrator("admin@admin.com", "admin", "Damian", "Kus", Sex.MALE);
		adminRepository.save(admin);
	}
}
