package com.medcontact.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.medcontact.data.model.Patient;
import com.medcontact.data.repository.PatientRepository;

@RestController
@RequestMapping(value="patient")
public class PatientDataController {
	
	@Autowired
	PatientRepository patientRepository;
	
	@RequestMapping(value="getPatientsList")
	@ResponseBody
	public List<Patient> getPatientsList() {
		ArrayList<Patient> patients = new ArrayList<>();
		patientRepository.findAll()
			.forEach(patients::add);
		
		return patients;
	}
}
