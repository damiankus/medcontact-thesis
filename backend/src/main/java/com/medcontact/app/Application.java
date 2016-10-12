package com.medcontact.app;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medcontact.data.model.Admin;
import com.medcontact.data.model.Doctor;
import com.medcontact.data.model.Patient;
import com.medcontact.data.model.ScheduleTimeSlot;
import com.medcontact.data.model.Specialty;
import com.medcontact.data.repository.AdministratorRepository;
import com.medcontact.data.repository.DoctorRepository;
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
	
	@Autowired
	DoctorRepository doctorRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<ScheduleTimeSlot> schedule = Arrays.asList(
				new ScheduleTimeSlot(DayOfWeek.MONDAY, "8:00:00", "12:00:00"));
		Specialty pulmonology = new Specialty();
		pulmonology.setCategory("choroby płuc");
		pulmonology.setName("pulmunologia");
		
		Doctor doctor = (Doctor) Doctor.getBuilder()
				.setSpecialties(Arrays.asList(pulmonology))
				.setBiography("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
				.setWeeklySchedule(schedule)
				.setUniversity("Uniwersytet Jagielloński")
				.setPassword(passwordEncoder.encode("haslo"))
				.setFirstName("Jan")
				.setLastName("Kowalski")
				.setEmail("jan.becwal@gmail.com")
				.build();
		
		schedule.forEach(ts -> ts.setDoctor(doctor));
		doctorRepository.save(doctor);
		
		Admin admin = (Admin) Admin.getBuilder()
				.setFirstName("Admin")
				.setLastName("Adminowski")
				.setEmail("admin@gmail.com")
				.setPassword(passwordEncoder.encode("haslo"))
				.build();
		
		adminRepository.save(admin);
		
		Patient patient = (Patient) Patient.getBuilder()
				.setFirstName("Gregory")
				.setLastName("House")
				.setEmail("house@gmail.com")
				.setPassword(passwordEncoder.encode("haslo"))
				.build();
		Patient patient2 = (Patient) Patient.getBuilder()
				.setFirstName("Damian")
				.setLastName("Kuś")
				.setEmail("dkus@gmail.com")
				.setPassword(passwordEncoder.encode("haslo"))
				.build();
		
		patientRepository.save(patient);
		patientRepository.save(patient2);
	}
}
