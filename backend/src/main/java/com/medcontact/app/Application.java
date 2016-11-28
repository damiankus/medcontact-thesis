package com.medcontact.app;

import com.medcontact.data.model.builders.DoctorBuilder;
import com.medcontact.data.model.domain.*;
import com.medcontact.data.repository.AdministratorRepository;
import com.medcontact.data.repository.DoctorRepository;
import com.medcontact.data.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
@ComponentScan(basePackages = {
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


        Specialty infectious = new Specialty();
        infectious.setName("choroby zakaźne");
        Specialty nephrology = new Specialty();
        nephrology.setName("nefrologia");

        Doctor doctor = (Doctor) DoctorBuilder.getBuilder()
                .setSpecialties(Arrays.asList(infectious, nephrology))
                .setBiography("Dr. Gregory House (Hugh Laurie), the title character, "
                        + "heads the Department of Diagnostic Medicine. "
                        + "House describes himself as"
                        + " 'a board-certified diagnostician with a double specialty of"
                        + " infectious disease and nephrology'.")
                .setUniversity("Princeton University")
                .setTitle("M.D.")
                .setPassword(passwordEncoder.encode("haslo"))
                .setFirstName("Gregory")
                .setLastName("House")
                .setEmail("house@gmail.com")
                .build();


        note.setDoctor(doctor);
        doctor.getNotes().add(note);

        doctor.setRoomId("default");
        doctorRepository.save(doctor);
    }
}
