package com.medcontact.app;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medcontact.data.model.builders.DoctorBuilder;
import com.medcontact.data.model.domain.Admin;
import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.domain.Note;
import com.medcontact.data.model.domain.Patient;
import com.medcontact.data.model.domain.Reservation;
import com.medcontact.data.model.domain.ScheduleTimeSlot;
import com.medcontact.data.model.domain.Specialty;
import com.medcontact.data.repository.AdministratorRepository;
import com.medcontact.data.repository.DoctorRepository;
import com.medcontact.data.repository.PatientRepository;

@SpringBootApplication
@ComponentScan(basePackages={
		"com.medcontact.controller", 
		"com.medcontact.security.config", 
		"com.medcontact.data.config",
		"com.medcontact.websocket"
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
		Admin admin = (Admin) Admin.getBuilder()
				.setFirstName("Admin")
				.setLastName("Adminowski")
				.setEmail("admin@gmail")
				.setPassword(passwordEncoder.encode("haslo"))
				.build();
		
		adminRepository.save(admin);
		
		Patient patient = (Patient) Patient.getBuilder()
				.setFirstName("Jan")
				.setLastName("Kowalski")
				.setEmail("kowal@gmail.com")
				.setPassword(passwordEncoder.encode("haslo"))
				.build();
		Patient patient2 = (Patient) Patient.getBuilder()
				.setFirstName("Damian")
				.setLastName("Drugi")
				.setEmail("dkus@gmail.com")
				.setPassword(passwordEncoder.encode("haslo"))
				.build();
		patientRepository.save(patient);
		patientRepository.save(patient2);
		
		Note note = new Note();
		note.setContent("Notatka probna");
		note.setTitle("NOTATKA PACJENT 1");
		note.setUploadTime(Timestamp.valueOf(LocalDateTime.now()));
		note.setPatient(patient);
		
		
		Specialty pulmonology = new Specialty();
		pulmonology.setCategory("choroby płuc");
		pulmonology.setName("pulmunologia");
		
		Doctor doctor = (Doctor) DoctorBuilder.getBuilder()
				.setSpecialties(Arrays.asList(pulmonology))
				.setBiography("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
				.setUniversity("Uniwersytet Jagielloński")
				.setPassword(passwordEncoder.encode("haslo"))
				.setFirstName("Gregory")
				.setLastName("House")
				.setEmail("house@gmail.com")
				.build();
		
		note.setDoctor(doctor);
		doctor.getNotes().add(note);
		LocalDateTime currentTime = LocalDateTime.now();
		
		for (int i = 0; i < 5; i++) {
			Reservation reservation = new Reservation();
			reservation.setStartDateTime(currentTime);
			reservation.setEndDateTime(currentTime.plusMinutes(5));
			reservation.setPatient(patient);
			reservation.setDoctor(doctor);
			doctor.addReservation(reservation);
			
			currentTime = currentTime.plusMinutes(5);
		}
		

		List<ScheduleTimeSlot> schedule = Arrays.asList(
				new ScheduleTimeSlot(doctor, LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
						LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0))));

		schedule.forEach(ts -> ts.setDoctor(doctor));
		doctorRepository.save(doctor);
	}
}
