package com.medcontact.app;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
import com.medcontact.data.model.Note;
import com.medcontact.data.model.Patient;
import com.medcontact.data.model.Reservation;
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
		Admin admin = (Admin) Admin.getBuilder()
				.setFirstName("Admin")
				.setLastName("Adminowski")
				.setEmail("admin@gmail.com")
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
		
		Reservation reservation = new Reservation();
		reservation.setDate(Date.valueOf(LocalDate.now().plus(10, ChronoUnit.DAYS)));
		reservation.setStartTime(Time.valueOf(LocalTime.NOON));
		reservation.setEndTime(Time.valueOf(LocalTime.of(13, 0)));
		reservation.setPatient(patient);
		
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
				.setFirstName("Gregory")
				.setLastName("House")
				.setEmail("house@gmail.com")
				.build();
		
		note.setOwner(doctor);
		doctor.getNotes().add(note);
		doctor.getReservations().add(reservation);
		reservation.setDoctor(doctor);
		
		schedule.forEach(ts -> ts.setDoctor(doctor));
		doctorRepository.save(doctor);
	}
}
