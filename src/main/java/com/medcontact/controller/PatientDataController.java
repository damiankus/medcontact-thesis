package com.medcontact.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.medcontact.data.model.FileEntry;
import com.medcontact.data.model.Opinion;
import com.medcontact.data.model.Patient;
import com.medcontact.data.model.Reservation;
import com.medcontact.data.repository.FileRepository;
import com.medcontact.data.repository.PatientRepository;

@RestController
@RequestMapping(value="patient")
public class PatientDataController {
	
	@Autowired
	PatientRepository patientRepository;
	
	@Autowired
	FileRepository fileRepository;
	
	@GetMapping(value="all")
	@ResponseBody
	public List<Patient> getAllPatients() {
		ArrayList<Patient> patients = new ArrayList<>();
		patientRepository.findAll()
			.forEach(patients::add);
		
		return patients;
	}
	
	@GetMapping(value="basic")
	@ResponseBody
	public Patient getBasicData() {
		return getCurrentUser();
	}
	
	@GetMapping(value="opinions")
	@ResponseBody
	public List<Opinion> getOpinions() {
		return getCurrentUser().getOpinions();
	}
	
	@GetMapping(value="reservations")
	@ResponseBody
	public List<Reservation> getReservations() {
		return getCurrentUser().getReservations();
	}
	
	@GetMapping(value="files")
	@ResponseBody
	public List<FileEntry> getFiles() {
		return getCurrentUser().getFiles();
	}
	
	@PostMapping(value="file/upload")
	public void handleFileUpload(
			@RequestParam("file") MultipartFile file) 
			throws SerialException, SQLException, IOException {
		
		FileEntry fileEntry = new FileEntry();
		fileEntry.setName(file.getName());
		fileEntry.setUploadTime(
				Timestamp.valueOf(
						LocalDateTime.now()));
		fileEntry.setFileContent(
				new SerialBlob(file.getBytes()));
		fileEntry.setFileOwner(getCurrentUser());
		fileRepository.save(fileEntry);
	}
	
	/* A utility method fetching the current logged in user's data. */
	
	private Patient getCurrentUser() {
		Patient patient = (Patient) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		
		/* We don't return the actual password. */
		
		patient.setPassword("");
		SecurityContextHolder.getContext()
			.getAuthentication()
			.getAuthorities()
			.forEach(a -> System.out.println(a));
		System.out.println("PATIENT: " + patient.toString() + "]");
		patient.getAuthorities().forEach(a -> System.out.println("\t" + a));
		System.out.println("]");
		
		return patient;
	}
}
