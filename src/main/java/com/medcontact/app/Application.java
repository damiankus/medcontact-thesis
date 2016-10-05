package com.medcontact.app;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.medcontact.data.model.Admin;
import com.medcontact.data.model.Doctor;
import com.medcontact.data.model.Patient;
import com.medcontact.data.model.ScheduleTimeSlot;
import com.medcontact.data.model.Sex;
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
				.setUsername("doctor")
				.setPassword("doctor")
				.setFirstName("Jan")
				.setLastName("Kowalski")
				.setEmail("jan.becwal@gmail.com")
				.setSex(Sex.MALE)
				.build();
		schedule.forEach(ts -> ts.setDoctor(doctor));
		
		doctorRepository.save(doctor);
		
		Admin admin = (Admin) Admin.getBuilder()
				.setFirstName("Jan")
				.setLastName("Kowalski")
				.setEmail("jan.kowalski@gmail.com")
				.setSex(Sex.MALE)
				.setUsername("admin")
				.setPassword("admin")
				.build();
		
		adminRepository.save(admin);
		
		Patient patient1 = (Patient) Patient.getBuilder()
				.setFirstName("Jan")
				.setLastName("Kowalski")
				.setEmail("jan.kowalski@gmail.com")
				.setSex(Sex.MALE)
				.setUsername("jank")
				.setPassword("haslo")
				.build();
		
		Patient patient2 = (Patient) Patient.getBuilder()
				.setFirstName("Janina")
				.setLastName("Malinowska")
				.setEmail("janina.malinowska@gmail.com")
				.setSex(Sex.MALE)
				.setUsername("janinamal")
				.setPassword("haslo")
				.build();
		
		patientRepository.save(patient1);
		patientRepository.save(patient2);
				
	}
}
