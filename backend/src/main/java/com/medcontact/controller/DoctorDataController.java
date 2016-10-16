package com.medcontact.controller;

import java.util.logging.Logger;

import org.json.simple.JSONObject;
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
	public ResponseEntity<JSONObject> addDoctor(
			@PathVariable("id") Long id) {
		
		JSONObject body = new JSONObject();
		HttpStatus status = HttpStatus.OK;
		ResponseEntity<JSONObject> response = new ResponseEntity<JSONObject>(body, status);
		
		if (!doctorRepository.exists(id)) {
			body.put("message", "INVALID_ID");
			status = HttpStatus.BAD_REQUEST;
		} else {
			body.put("message", "OK");
			body.put("doctor", (BasicUser) doctorRepository.findOne(id));
			status = HttpStatus.OK;
		}

		return response;
	}
}
