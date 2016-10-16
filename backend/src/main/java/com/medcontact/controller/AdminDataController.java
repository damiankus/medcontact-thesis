package com.medcontact.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medcontact.data.model.Doctor;
import com.medcontact.data.repository.BasicUserRepository;
import com.medcontact.data.repository.DoctorRepository;

@RestController
@RequestMapping("admins")
public class AdminDataController {
	private Logger logger = Logger.getLogger(AdminDataController.class.getName());
	
	@Autowired
	BasicUserRepository userRepository;
	
	@Autowired 
	DoctorRepository doctorRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@PostMapping("new/doctor")
	public ResponseEntity<Map<String, String>> addDoctor(
			@RequestBody Doctor doctor) {
		
		Map<String, String> body = new HashMap<>();
		HttpStatus status = HttpStatus.CREATED;
		ResponseEntity<Map<String, String>> response = new ResponseEntity<>(body, status);
		
		logger.warning(doctor.toString());
		
		if (!userRepository.isEmailAvailable(doctor.getEmail())) {
			body.put("error_message", "EMAIL_TAKEN");
			status = HttpStatus.CONFLICT;
		} else {
			doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
			doctorRepository.save(doctor);
			body.put("id", "" + doctor.getId());
			status = HttpStatus.CREATED;
		}
		
		return response;
	}
}
