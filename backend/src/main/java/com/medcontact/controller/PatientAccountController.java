package com.medcontact.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.medcontact.data.model.ConnectionData;
import com.medcontact.data.model.Doctor;
import com.medcontact.data.model.FileEntry;
import com.medcontact.data.model.Patient;
import com.medcontact.data.model.Reservation;
import com.medcontact.data.repository.FileRepository;
import com.medcontact.data.repository.PatientRepository;
import com.medcontact.data.repository.ReservationRepository;

import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping(value="patients")
public class PatientAccountController {
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Getter
	@Setter
	@Value("${general.files-path-root}")
	private String userFilesPathRoot; 
	
	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Value("${webrtc.turn.api-endpoint}")
	private String turnEndpoint;

	@Value("${webrtc.turn.username}")
	private String turnUsername;

	@Value("${webrtc.turn.domain}")
	private String turnDomain;

	@Value("${webrtc.turn.application}")
	private String turnApplicationName;

	@Value("${webrtc.turn.secret}")
	private String turnSecret;

	@GetMapping("{patientId}/connection/{consultationId}")
	public ResponseEntity<ConnectionData> getConnectionData(
			@PathVariable("patientId") Long patientId,
			@PathVariable("consultationId") Long reservationId) {

		ConnectionData body = null;
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Patient patient = patientRepository.findOne(patientId);

		if (!patientId.equals(patient.getId())) {
			status = HttpStatus.BAD_REQUEST;

		} else {
			Reservation reservation = reservationRepository.findOne(reservationId);
			LocalDate currentDate = LocalDate.now();
			Time currentTime = Time.valueOf(LocalTime.now());

			if (reservation == null) {
				status = HttpStatus.BAD_REQUEST;
				logger.warn("No reservation with specified ID found");

			} else {
				Doctor doctor = reservation.getDoctor();

				if (!patientId.equals(reservation.getPatient().getId())) {
					status = HttpStatus.BAD_REQUEST;
					logger.warn("Invalid patient ID for the specified reservation");

				} else if (!(currentDate.isEqual(reservation.getDate().toLocalDate())
						&& currentTime.after(reservation.getStartTime())
						&& currentTime.before(reservation.getEndTime()))) {

					status = HttpStatus.BAD_REQUEST;
					logger.warn("Invalid date or time of the reservation");

				} else if (doctor.isBusy()) {
					status = HttpStatus.BAD_REQUEST;
					logger.warn("Doctor is currently busy");

				} else {
					body = new ConnectionData();
					body.setEndpoint(turnEndpoint);
					body.setUsername(turnUsername);
					body.setDomain(turnDomain);
					body.setApplication(turnApplicationName);
					body.setSecret(turnSecret);
					body.setRoomId(doctor.getRoomId());
				}
			}
		}

		return new ResponseEntity<>(body, status);
	}

	@PostMapping(value="{id}/files")
	public void handleFileUpload(
			@PathVariable("id") Long patientId,
			@RequestParam("files") List<MultipartFile> files)
			throws SerialException, SQLException, IOException {

		Patient patient = getCurrentUser();
		System.out.println(files.get(0).getOriginalFilename());

		if (patient.getId().equals(patientId)) {
			patient = patientRepository.findOne(patientId);
			
			for (MultipartFile file : files) {
				String filePath = userFilesPathRoot 
						+ File.separator 
						+ patientId
						+ File.separator
						+ file.getOriginalFilename();
				
				FileEntry fileEntry = new FileEntry();
				fileEntry.setName(file.getOriginalFilename());
				fileEntry.setUploadTime(
						Timestamp.valueOf(
								LocalDateTime.now()));
				fileEntry.setUrl(filePath);
				fileEntry.setFileOwner(patient);
				fileEntry.setContentType(file.getContentType());
				patient.getFiles().add(fileEntry);
				fileRepository.save(fileEntry);
				
				File fileToWrite = Paths.get(filePath).toAbsolutePath().toFile();
				fileToWrite.getParentFile().mkdirs();
				fileToWrite.createNewFile();
				
				try (FileOutputStream out = new FileOutputStream(fileToWrite)) {
					out.write(file.getBytes());
				}
				
				System.out.println("Done, file saved under: " + Paths.get(filePath).toAbsolutePath().toString());
			}
		} else {
			logger.warn("Detected an attempt to upload a file without authorization");
		}
	}

	/* A utility method fetching the current logged in user's data. */

	private Patient getCurrentUser() {
		Patient patient = (Patient) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();

		return patient;
	}
}
