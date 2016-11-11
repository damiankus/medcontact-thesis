package com.medcontact.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.domain.ScheduleTimeSlot;
import com.medcontact.data.model.dto.BasicDoctorDetails;
import com.medcontact.data.model.dto.ScheduleShortData;
import com.medcontact.data.repository.BasicUserRepository;
import com.medcontact.data.repository.DoctorRepository;
import com.medcontact.data.validation.DoctorValidator;
import com.medcontact.data.validation.ValidationResult;
import com.medcontact.exception.UserNotFoundException;

@RestController
@RequestMapping("doctors")
public class DoctorDataController {
	private Logger logger = Logger.getLogger(DoctorDataController.class.getName());
	private DoctorValidator doctorValidator = new DoctorValidator();

	@Autowired
	BasicUserRepository userRepository;
	
	@Autowired 
	DoctorRepository doctorRepository;
	
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
	private PasswordEncoder passwordEncoder;

	@PostMapping("")
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
						.field("secure", 1)
						.asJson();
				
				/* 201 - CREATED */
				
				if (jsonResponse.getStatus() != 201) {
					System.out.println(jsonResponse.getStatusText().toLowerCase());
					System.out.println(HttpStatus.CREATED.toString().toLowerCase());
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
		}
		
		return new ResponseEntity<>(body, status);
	}

	@GetMapping(value = "")
	@ResponseBody
	public List<BasicDoctorDetails> getDoctors() {
		return doctorRepository.findAll()
				.stream()
				.map(BasicDoctorDetails::new)
				.collect(Collectors.toList());
	}

	@PostMapping(value = "{id}/schedules")
	@ResponseStatus(value = HttpStatus.CREATED, reason = "Schedule added.")
	public void saveDoctorSchedule(@RequestBody ScheduleShortData schedule, @PathVariable("id") Long id) {
		ScheduleTimeSlot scheduleTimeSlot = new ScheduleTimeSlot(schedule.getStart(), schedule.getEnd());
		Doctor doctor = doctorRepository.findOne(id);
		doctor.addSchedule(scheduleTimeSlot);
		doctorRepository.save(doctor);
	}

	@GetMapping(value = "{id}/schedules")
	@ResponseBody
	public List<ScheduleTimeSlot> getSpecificDoctorsSchedules(@PathVariable("id") Long id) {
		return doctorRepository.findOne(id).getWeeklySchedule();
	}
	
	@GetMapping(value = "{id}/busy")
	@ResponseBody
	public boolean isDoctorBusy(
			@PathVariable("id") Long doctorId) throws UserNotFoundException {
		
		Doctor doctor = doctorRepository.findOne(doctorId);
		if (doctor == null) {
			throw new UserNotFoundException();
		}
		
		return doctor.isBusy();
	}
}	
