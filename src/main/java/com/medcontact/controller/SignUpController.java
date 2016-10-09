package com.medcontact.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.medcontact.data.model.BasicUser;
import com.medcontact.data.model.BasicUserDetails;
import com.medcontact.data.model.Patient;
import com.medcontact.data.repository.BasicUserRepository;
import com.medcontact.data.repository.PatientRepository;

@Controller
@RequestMapping(value="signup")
public class SignUpController {

	@Autowired 
	private BasicUserRepository userRepository;
	
	@Autowired 
	private PatientRepository patientRepository;
	
	@GetMapping(value="")
	public String getSignUpForm() {
		return "signup";
	}
	
	@PostMapping(value="save")
	public ResponseEntity<String> saveUser(BasicUserDetails userDetails) {
		
		Optional<BasicUser> user = userRepository.findByEmail(
				userDetails.getEmail());
		ResponseEntity<String> response;
		
		if (user.isPresent()) {
			response = new ResponseEntity<String>("Username has been taken.", HttpStatus.CONFLICT);
		} else {
			response = new ResponseEntity<>("Account has been created.", HttpStatus.OK);
			Patient patient = (Patient) Patient.getBuilder()
					.valueOf(userDetails)
					.build();
			patientRepository.save(patient);
		}
		
		return response;
	}
}
