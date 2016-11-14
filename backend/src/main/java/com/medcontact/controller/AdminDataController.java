package com.medcontact.controller;

import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.dto.DoctorCreateData;
import com.medcontact.data.repository.BasicUserRepository;
import com.medcontact.data.repository.DoctorRepository;
import com.medcontact.exception.EmailTakenException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("admins")
@Data
public class AdminDataController {
    private Logger logger = Logger.getLogger(AdminDataController.class.getName());

    @Autowired
    private BasicUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DoctorRepository doctorRepository;

    @PostMapping(value = "doctors")
    @ResponseStatus(value = HttpStatus.CREATED, reason = "Doctor has been created.")
    @ResponseBody
    public void addNewDoctor(@RequestBody DoctorCreateData doctorCreateData) {
        if (userRepository.findByEmail(
                doctorCreateData.getEmail())
                .isPresent()) {
            throw new EmailTakenException(doctorCreateData.getEmail());
        }
        String encodedPassword = passwordEncoder.encode(doctorCreateData.getPassword());
        Doctor doctor = new Doctor(doctorCreateData, encodedPassword);
        doctorRepository.save(doctor);
    }
}
