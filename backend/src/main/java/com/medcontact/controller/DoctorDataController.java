package com.medcontact.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medcontact.data.model.BasicUser;
import com.medcontact.data.repository.BasicUserRepository;
import com.medcontact.data.repository.DoctorRepository;

@RestController
@RequestMapping("doctor")
public class DoctorDataController {
private Logger logger = Logger.getLogger(DoctorDataController.class.getName());
	
	@Autowired
	BasicUserRepository userRepository;
	
	@Autowired 
	DoctorRepository doctorRepository;
	
	@GetMapping("{id}")
	public ResponseEntity<Map<String, Object>> addDoctor(
			@PathVariable("id") Long id) {
		
		Map<String, Object> body = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(body, status);
		
		if (!doctorRepository.exists(id)) {
			body.put("errors", Arrays.asList("Invalid ID"));
			status = HttpStatus.BAD_REQUEST;
		} else {
			body.put("message", "OK");
			body.put("doctor", (BasicUser) doctorRepository.findOne(id));
			status = HttpStatus.OK;
		}

		return response;
	}
}
