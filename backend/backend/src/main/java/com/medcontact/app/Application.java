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
        "com.medcontact.mail",
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
                .setEmail("a1@test.com")
                .setPassword(passwordEncoder.encode("haslo"))
                .build();

        adminRepository.save(admin);

        Patient patient1 = (Patient) Patient.getBuilder()
                .setFirstName("Jan")
                .setLastName("Kowalski")
                .setEmail("p1@test.com")
                .setPassword(passwordEncoder.encode("haslo"))
                .build();
        patientRepository.save(patient1);
        
        Patient patient2 = (Patient) Patient.getBuilder()
                .setFirstName("Anna")
                .setLastName("Malinowska")
                .setEmail("p2@test.com")
                .setPassword(passwordEncoder.encode("haslo"))
                .build();
        patientRepository.save(patient2);
        
        Patient patient3 = (Patient) Patient.getBuilder()
                .setFirstName("Agata")
                .setLastName("Rusin")
                .setEmail("p3@test.com")
                .setPassword(passwordEncoder.encode("haslo"))
                .build();
        patientRepository.save(patient3);

        Note note = new Note();
        note.setContent("Notatka probna");
        note.setUploadTime(Timestamp.valueOf(LocalDateTime.now()));
        note.setPatient(patient1);

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
                .setEmail("d1@test.com")
                .build();

        note.setDoctor(doctor);
        doctor.getNotes().add(note);

        doctor.setRoomId("d1-room");
        doctorRepository.save(doctor);

        Specialty specialty2 = new Specialty();
        specialty2.setName("pneumologia");
        
        Doctor doctor2 = (Doctor) DoctorBuilder.getBuilder()
                .setSpecialties(Arrays.asList(specialty2))
                .setBiography("Lorem ipsum dolor sit amet, "
                		+ "consectetur adipiscing elit, sed do eiusmod"
                		+ " tempor incididunt ut labore et dolore magna"
                		+ " aliqua. Ut enim ad minim veniam, quis nostrud"
                		+ " exercitation ullamco laboris nisi ut aliquip"
                		+ " ex ea commodo consequat. Duis aute irure dolor"
                		+ " in reprehenderit in voluptate velit esse"
                		+ " cillum dolore eu fugiat nulla pariatur."
                		+ " Excepteur sint occaecat cupidatat non proident,"
                		+ " sunt in culpa qui officia deserunt mollit anim id est laborum.")
                .setUniversity("Śląski Uniwersytet Medyczny")
                .setTitle("dr")
                .setPassword(passwordEncoder.encode("haslo"))
                .setFirstName("Anna")
                .setLastName("Kowalczyk")
                .setEmail("d2@test.com")
                .build();
        
        doctor2.setRoomId("d2-room");
        doctorRepository.save(doctor2);
        
        Specialty specialty3 = new Specialty();
        specialty3.setName("dermatologia");
        
        Doctor doctor3 = (Doctor) DoctorBuilder.getBuilder()
                .setSpecialties(Arrays.asList(specialty3))
                .setBiography("Lorem ipsum dolor sit amet, "
                		+ "consectetur adipiscing elit, sed do eiusmod"
                		+ " tempor incididunt ut labore et dolore magna"
                		+ " aliqua. Ut enim ad minim veniam, quis nostrud"
                		+ " exercitation ullamco laboris nisi ut aliquip"
                		+ " ex ea commodo consequat. Duis aute irure dolor"
                		+ " in reprehenderit in voluptate velit esse"
                		+ " cillum dolore eu fugiat nulla pariatur."
                		+ " Excepteur sint occaecat cupidatat non proident,"
                		+ " sunt in culpa qui officia deserunt mollit anim id est laborum.")
                .setTitle("dr")
                .setUniversity("Uniwersytet Jagielloński")
                .setPassword(passwordEncoder.encode("haslo"))
                .setFirstName("Jan")
                .setLastName("Malinowski")
                .setEmail("d3@test.com")
                .build();
        
        doctor3.setRoomId("d3-room");
        doctorRepository.save(doctor3);
    }
}
