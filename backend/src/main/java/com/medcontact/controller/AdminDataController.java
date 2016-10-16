package com.medcontact.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.medcontact.data.model.Doctor;
import com.medcontact.data.repository.BasicUserRepository;
import com.medcontact.data.repository.DoctorRepository;
import com.medcontact.data.validation.DoctorValidator;
import com.medcontact.data.validation.ValidationResult;

import lombok.Data;

@RestController
@RequestMapping("admins")
@Data
public class AdminDataController {
	private Logger logger = Logger.getLogger(AdminDataController.class.getName());
	private DoctorValidator doctorValidator = new DoctorValidator();
	
	/* Bind the configuration data from the properties file. */
	
	@Value("${webrtc.turn.api-endpoint}")
	private String turnServerAddress;
	
	@Value("${webrtc.turn.username}")
	private String turnUsername;

	@Value("${webrtc.turn.domain}")
	private String turnDomain;
	
	@Value("${webrtc.turn.application}")
	private String turnApplicationName;
	
	@Value("${webrtc.turn.secret}")
	private String turnSecret;
	
	@Autowired
	private BasicUserRepository userRepository;
	
	@Autowired 
	private DoctorRepository doctorRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("new/doctor")
	public ResponseEntity<Map<String, Object>> addDoctor(
			@RequestBody Doctor doctor) throws UnirestException {
		
		Map<String, Object> body = new HashMap<>();
		HttpStatus status = HttpStatus.CREATED;
		
		if (!userRepository.isEmailAvailable(doctor.getEmail())) {
			body.put("errors", Arrays.asList("Email already taken"));
			status = HttpStatus.CONFLICT;
			
		} else {
			ValidationResult validationResult = doctorValidator.validate(doctor);
			
			if (!validationResult.isValid()) {
				body.put("errors", validationResult.getErrors());
				status = HttpStatus.BAD_REQUEST;
			} else {
				
				/* Make a GET request to the TURN server mediating 
				 * in the audio-video communication in order to create 
				 * a new room for the doctor. */
				
				HttpResponse<JsonNode> jsonResponse = Unirest.post(turnServerAddress + "room")
						.field("ident", turnUsername)
						.field("secret", turnSecret)
						.field("domain", turnDomain)
						.field("application", turnApplicationName)
						.field("room", doctor.getRoomId())
						.asJson();
				
				if (!jsonResponse.getStatusText().equals(HttpStatus.OK.toString())) {
					body.put("errors", Arrays.asList("Couldn't create consultation room"));
					status = HttpStatus.SERVICE_UNAVAILABLE;
				}
				
				/* Replace the original password with a hashed one. */

				doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
				doctorRepository.save(doctor);
				body.put("id", "" + doctor.getId());
				status = HttpStatus.CREATED;
			}
		}
		
		return new ResponseEntity<>(body, status);
	}
}
