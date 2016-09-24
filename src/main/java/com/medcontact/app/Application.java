package com.medcontact.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.medcontact.data.model.Admin;
import com.medcontact.data.model.FileEntry;
import com.medcontact.data.model.Patient;
import com.medcontact.data.model.Sex;
import com.medcontact.data.repository.AdministratorRepository;
import com.medcontact.data.repository.PatientRepository;

@SpringBootApplication
@ComponentScan(basePackages={
		"com.medcontact.controller", 
		"com.medcontact.security.config", 
		"com.medcontact.data.config"
})
public class Application implements CommandLineRunner {

	@Autowired
	AdministratorRepository adminRepository;
	
	@Autowired
	PatientRepository patientRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Admin admin = (Admin) Admin.getBuilder()
				.setUsername("admin")
				.setPassword("admin")
				.setFirstName("Damian")
				.setLastName("Kus")
				.setEmail("damian.kus.main@gmail.com")
				.setSex(Sex.MALE)
				.build();
		adminRepository.save(admin);
		
		Patient patient = (Patient) Patient.getBuilder()
				.setUsername("dakus")
				.setPassword("password")
				.setFirstName("Damian")
				.setLastName("Kuś")
				.setEmail("damian.kus.main@gmail.com")
				.setSex(Sex.MALE)
				.build();
		patientRepository.save(patient);
	}
}
