package com.medcontact.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.springframework.core.io.FileSystemResource;
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
import com.medcontact.exception.UnauthorizedUserException;

import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping(value="patients")
public class PatientAccountController {
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Getter
	@Setter
	@Value("${general.files-path-root}")
	private String patientFilesPathRoot; 
	
	@Getter
	@Setter
	@Value("${general.host}")
	private String host;
	
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
			throws UnauthorizedUserException, SerialException, SQLException, IOException {

		if (isEntitled(patientId)) {
			
			/* We have to load patient from the repository
			 * because using principal object from the 
			 * authentication context causes throwing an lazy 
			 * loading exception */
			
			Patient patient = patientRepository.findOne(patientId);
			
			for (MultipartFile file : files) {
				String filePath = patientFilesPathRoot 
						+ File.separator 
						+ patientId
						+ File.separator
						+ file.getOriginalFilename();
				
				String fileUrl = patientFilesPathRoot 
						+ "/" + patientId + "/files/";
				
				FileEntry fileEntry = new FileEntry();
				fileEntry.setName(file.getOriginalFilename());
				fileEntry.setUploadTime(
						Timestamp.valueOf(
								LocalDateTime.now()));
				fileEntry.setFileOwner(patient);
				fileEntry.setContentType(file.getContentType());
				fileEntry.setUrl(fileUrl);
				fileEntry.setPath(Paths.get(filePath).toAbsolutePath().toString());
				patient.getFileEntries().add(fileEntry);

				fileEntry = fileRepository.save(fileEntry);
				fileEntry.setUrl(fileUrl + fileEntry.getId());
				fileRepository.save(fileEntry);
				
				File fileToWrite = Paths.get(filePath).toAbsolutePath().toFile();
				fileToWrite.getParentFile().mkdirs();
				fileToWrite.createNewFile();
				
				System.out.println("File name: " + file.getName());
				System.out.println("File original name: " + file.getOriginalFilename());
				System.out.println("File path  " + filePath);
				System.out.println(fileToWrite.getAbsolutePath());
				
				try (FileOutputStream out = new FileOutputStream(fileToWrite)) {
					out.write(file.getBytes());
				}
				
				logger.info("Done, file saved under: " + Paths.get(filePath).toAbsolutePath().toString());
			}
		}
	}
	
	
	@GetMapping(value="{id}/files/{fileId}")
	public FileSystemResource getFile(
			@PathVariable("id") Long patientId,
			@PathVariable("fileId") Long fileId) 
					throws UnauthorizedUserException, FileNotFoundException {
		
		if (isEntitled(patientId)) {
			FileEntry fileEntry = fileRepository.findOne(fileId);
			
			if (fileEntry != null) {
				return new FileSystemResource(fileEntry.getPath());
			}
		}
		
		throw new FileNotFoundException();
	}
	
	/* A utility method checking if a user with the given ID is entitled to 
	 * obtain access to a resource */

	private boolean isEntitled(Long patientId) throws UnauthorizedUserException {
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		
		if (!(principal instanceof Patient) 
			|| (!((Patient) principal).getId().equals(patientId))) {
			
			logger.warn("Detected an attempt to upload a file without authorization " + principal.toString());
			throw new UnauthorizedUserException();
		}
		
		return true;
	}
}
