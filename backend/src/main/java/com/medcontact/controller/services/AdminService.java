package com.medcontact.controller.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.medcontact.data.model.domain.Patient;
import com.medcontact.data.model.dto.PersonalDataPassword;
import com.medcontact.data.repository.AdministratorRepository;
import com.medcontact.exception.NotMatchedPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.medcontact.data.model.domain.Admin;
import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.repository.BasicUserRepository;
import com.medcontact.data.repository.DoctorRepository;
import com.medcontact.exception.UnauthorizedUserException;
import com.medcontact.security.config.EntitlementValidator;

@Service
public class AdminService {
    private Logger logger = Logger.getLogger(AdminService.class.getName());
    
    @Autowired
    BasicUserRepository userRepository;

    @Autowired
    DoctorRepository doctorRepository;
    
    @Autowired
    private EntitlementValidator entitlementValidator;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${webrtc.turn.api-endpoint}")
    private String turnApiEndpoint;

    @Value("${webrtc.turn.ident}")
    private String turnIdent;

    @Value("${webrtc.turn.domain}")
    private String turnDomain;

    @Value("${webrtc.turn.application}")
    private String turnApplicationName;

    @Value("${webrtc.turn.secret}")
    private String turnSecret;
    
    public ResponseEntity<Map<String, Object>> addDoctor(Long adminId, Doctor doctor) throws UnirestException, UnauthorizedUserException {
    	entitlementValidator.isEntitled(adminId, Admin.class);
    		
    	Map<String, Object> body = new HashMap<>();
        HttpStatus status = HttpStatus.CREATED;
        
        System.out.println("\n\n\n (email): " + doctor.getUsername());
        System.out.println("email present? " + userRepository.findByUsername(doctor.getUsername()).isPresent());
        System.out.println("\n\n\n");
        
        if (userRepository.findByUsername(doctor.getUsername()).isPresent()) {
            body.put("errors", Arrays.asList("Email already taken"));
            status = HttpStatus.BAD_REQUEST;

        } else {

			/* Make a GET request to the TURN server mediating
			 * in the audio-video communication in order to create
			 * a new room for the doctor. */

            HttpResponse<JsonNode> jsonResponse = Unirest.post(turnApiEndpoint + "room")
                    .field("ident", turnIdent)
                    .field("secret", turnSecret)
                    .field("domain", turnDomain)
                    .field("application", turnApplicationName)
                    .field("room", doctor.getRoomId())
                    .field("secure", 1)
                    .asJson();

			/* 201 - CREATED */

            if (jsonResponse.getStatus() != 201) {
                body.put("errors", Arrays.asList("Couldn't create consultation room"));
                status = HttpStatus.SERVICE_UNAVAILABLE;
                logger.warning("Couldn't create a room for the new doctor");

            } else {

				/* Replace the original password with a hashed one. */

                doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
                doctorRepository.save(doctor);
                body.put("id", "" + doctor.getId());
                status = HttpStatus.CREATED;
                logger.warning("A new room created successfully");
            }
        }

        return new ResponseEntity<>(body, status);
    }

    public void changePersonalData(Long adminId, PersonalDataPassword personalDataPassword) {
        Admin admin = (Admin)userRepository.findOne(adminId);

        admin.changePersonalData(personalDataPassword, passwordEncoder);
        if(personalDataPassword.getNewPassword1() != null && personalDataPassword.getNewPassword1().equals(personalDataPassword.getNewPassword2())){
            if (passwordEncoder.matches(personalDataPassword.getOldPassword(), admin.getPassword())){
                admin.setPassword(passwordEncoder.encode(personalDataPassword.getNewPassword1()));
            }
            else{
                throw new NotMatchedPasswordException();
            }
        }
        userRepository.save(admin);
    }
}
