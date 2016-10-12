package com.medcontact.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@GetMapping(value="")
	public String getSignUpForm() {
		return "signup";
	}
	
	@PostMapping(value="save", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveUser(BasicUserDetails userDetails) {
		
		Optional<BasicUser> user = userRepository.findByEmail(
				userDetails.getEmail());
		ResponseEntity<String> response;
		
		if (user.isPresent()) {
			response = new ResponseEntity<String>("The specified email has been taken.", HttpStatus.CONFLICT);
		} else {
			response = new ResponseEntity<>("Account has been created.", HttpStatus.OK);
			Patient patient = (Patient) Patient.getBuilder()
					.valueOf(userDetails)
					.setPassword(passwordEncoder.encode(userDetails.getPassword()))
					.build();
			patientRepository.save(patient);
		}
		
		return response;
	}
	
	@PostMapping(value="email/available")
	public ResponseEntity<String> isEmailAvailable(
			@RequestParam("email") String email) {
		
		boolean emailAvailable = userRepository.isEmailAvailable(email);
		ResponseEntity<String> response;
		
		if (emailAvailable) {
			response = new ResponseEntity<String>("Email taken.", HttpStatus.CONFLICT);
		} else {
			response = new ResponseEntity<>("Email available.", HttpStatus.OK);
		}
		
		return response;
	}
}
