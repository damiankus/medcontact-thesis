package com.medcontact.controller;

import com.medcontact.data.model.BasicUser;
import com.medcontact.data.model.Patient;
import com.medcontact.data.repository.BasicUserRepository;
import com.medcontact.data.repository.PatientRepository;
import com.medcontact.exception.EmailTakenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

	@PostMapping(value = "save")
	@ResponseStatus(value = HttpStatus.CREATED, reason = "Account has been created.")
	@ResponseBody
	public void saveUser(@RequestBody BasicUser user) {

		if (userRepository.findByEmail(
				user.getEmail())
				.isPresent()) {
			throw new EmailTakenException(user.getEmail());
		}
		Patient patient = (Patient) Patient.getBuilder()
				.valueOf(user)
				.setPassword(passwordEncoder.encode(user.getPassword()))
				.build();
		patientRepository.save(patient);
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
