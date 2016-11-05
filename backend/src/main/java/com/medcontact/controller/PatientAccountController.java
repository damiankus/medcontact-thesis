package com.medcontact.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.domain.FileEntry;
import com.medcontact.data.model.domain.Patient;
import com.medcontact.data.model.domain.Reservation;
import com.medcontact.data.model.dto.BasicDoctorDetails;
import com.medcontact.data.model.dto.ConnectionData;
import com.medcontact.data.repository.DoctorRepository;
import com.medcontact.data.repository.FileRepository;
import com.medcontact.data.repository.PatientRepository;
import com.medcontact.data.repository.ReservationRepository;
import com.medcontact.exception.UnauthorizedUserException;

import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping(value = "patients")
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
    private DoctorRepository doctorRepository;

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
            LocalDateTime currentDateTime = LocalDateTime.now();

            if (reservation == null) {
                status = HttpStatus.BAD_REQUEST;
                logger.warn("No reservation with specified ID found");

            } else {
                Doctor doctor = reservation.getDoctor();

                if (!patientId.equals(reservation.getPatient().getId())) {
                    status = HttpStatus.BAD_REQUEST;
                    logger.warn("Invalid patient ID for the specified reservation");

                } else if (currentDateTime.isAfter(reservation.getStartDateTime())
                        && currentDateTime.isBefore(reservation.getEndDateTime())) {

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

    @PostMapping(value = "{id}/files")
    public void handleFileUpload(
            @PathVariable("id") Long patientId,
            @RequestParam("files") List<MultipartFile> files)
            throws UnauthorizedUserException, SerialException, SQLException, IOException {

        if (isEntitled(patientId)) {

			/* We have to load patient from the repository
             * because using principal object from the
			 * authentication context causes throwing an lazy 
			 * loading exception */
			
        	System.out.println("Uploaded files: " + files.size());
			Patient patient = patientRepository.findOne(patientId);
			
			for (MultipartFile file : files) {
				String filePath = String.format(
						"%s/%d/%s", 
						patientFilesPathRoot,
						patientId,
						file.getOriginalFilename());
				
				String fileUrl = String.format(
						"%s%s/%d/files/", 
						host, 
						patientFilesPathRoot,
						patientId);
				
				List<FileEntry> foundEntries = fileRepository.findByFilenameAndOwnerId(
						file.getOriginalFilename(), patientId);
				
				FileEntry fileEntry = (foundEntries.size() > 0)
					? foundEntries.get(0)
					: new FileEntry();
					
				fileEntry.setName(file.getOriginalFilename());
				fileEntry.setUploadTime(
						Timestamp.valueOf(
								LocalDateTime.now()));
				fileEntry.setFileOwner(patient);
				fileEntry.setContentType(file.getContentType());
				fileEntry.setContentLength(file.getSize());
				fileEntry.setPath(Paths.get(filePath).toAbsolutePath().toString());
				patient.getFileEntries().add(fileEntry);

				fileEntry.setUrl(fileUrl + fileEntry.getId());
				fileEntry = fileRepository.save(fileEntry);

				/* If the entry is saved for the first time
				 * we need to update its URL */
				
				if (foundEntries.size() == 0) {
					System.out.println("ALREADY IN REPO");
					fileEntry.setUrl(fileUrl + fileEntry.getId());
					fileRepository.save(fileEntry);
				}
				
				File fileToWrite = Paths.get(filePath).toAbsolutePath().toFile();
				fileToWrite.getParentFile().mkdirs();
				fileToWrite.createNewFile();
				
				System.out.println("File name: " + file.getName());
				System.out.println("File original name: " + file.getOriginalFilename());
				System.out.println("File path  " + filePath);
				System.out.println(fileToWrite.getAbsolutePath());
				
				try (FileOutputStream out = new FileOutputStream(fileToWrite)) {
					Files.deleteIfExists(fileToWrite.toPath());
					Files.copy(file.getInputStream(), fileToWrite.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				
				logger.info("File saved under: " + Paths.get(filePath).toAbsolutePath().toString());
			}
		}
	}
	
	
	@GetMapping(value="{id}/files/{fileId}")
	public void getFile(
			@PathVariable("id") Long patientId,
			@PathVariable("fileId") Long fileId,
			HttpServletResponse response) 
					throws UnauthorizedUserException, IOException {
		
		if (isEntitled(patientId)) {
			FileEntry fileEntry = fileRepository.findOne(fileId);
			
			if (fileEntry != null) {
				response.setContentType(fileEntry.getContentType());
				response.setContentLengthLong(fileEntry.getContentLength());
				response.setHeader(
						"Content-Disposition",
					     String.format("attachment; filename=\"%s\"",
					                fileEntry.getName()));
						
				try (OutputStream out = response.getOutputStream();) {
					Files.copy(
							Paths.get(fileEntry.getPath()),out);
				}
			}
		}
	}

    @GetMapping(value = "{id}/doctors")
    @ResponseBody
    public List<BasicDoctorDetails> getDoctors(@PathVariable("id") Long patientId) {
        System.out.println(1);
        //TODO replace all doctor to doctor only visible to Patient
        return doctorRepository.findAll()
                .stream()
                .map(doctor -> {
                            System.out.println();
                            return new BasicDoctorDetails(doctor);
                        }
                ).collect(Collectors.toList());
    }

	/* A utility method checking if a user waith the given ID is entitled to
     * obtain access to a resource */

    private boolean isEntitled(Long patientId) throws UnauthorizedUserException {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof Patient)
                || (!((Patient) principal).getId().equals(patientId))) {

            logger.warn("An attempt to upload a file without authorization detected - " + principal.toString());
            throw new UnauthorizedUserException();
        }

        return true;
    }
}
