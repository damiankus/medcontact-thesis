package com.medcontact.controller;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.medcontact.data.model.domain.*;
import com.medcontact.data.model.dto.BasicDoctorDetails;
import com.medcontact.data.model.dto.ConnectionData;
import com.medcontact.data.model.dto.ReservationDate;
import com.medcontact.data.model.enums.ReservationState;
import com.medcontact.data.repository.BasicUserRepository;
import com.medcontact.data.repository.DoctorRepository;
import com.medcontact.data.repository.ReservationRepository;
import com.medcontact.data.repository.SharedFileRepository;
import com.medcontact.data.validation.DoctorValidator;
import com.medcontact.data.validation.ValidationResult;
import com.medcontact.exception.UnauthorizedUserException;
import com.medcontact.security.config.EntitlementValidator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("doctors")
public class DoctorDataController {
	private Logger logger = Logger.getLogger(DoctorDataController.class.getName());
	private DoctorValidator doctorValidator = new DoctorValidator();

    @Getter
    @Setter
    @Value("${general.host}")
    private String host;

	@Autowired
	private SimpMessagingTemplate brokerMessagingTemplate;

	@Autowired
	BasicUserRepository userRepository;

	@Autowired
	DoctorRepository doctorRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SharedFileRepository sharedFileRepository;

    @Autowired
    private EntitlementValidator entitlementValidator;

	/* Bind the configuration data from the properties file. */

	@Value("${webrtc.turn.api-endpoint}")
	private String turnApiEndpoint;

	@Value("${webrtc.turn.ident}")
	private String turnIdent;

	@Value("${webrtc.turn.domain}")
	private String turnDomain;

	@Value("${webrtc.turn.application}")
	private String turnApplicationName;

	@Value("${webrtc.turn.secret}")
	private String turnSecret;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping("{id}/info")
	public BasicDoctorDetails getDoctorInfo(
			@PathVariable("id") Long doctorId) throws IllegalArgumentException {
		
		Doctor doctor = doctorRepository.findOne(doctorId);
		
		if (doctor == null) {
			throw new IllegalArgumentException();
			
		} else {
			return new BasicDoctorDetails(doctor);
		}
	}
	
	@GetMapping("{doctorId}/connection")
    public ResponseEntity<ConnectionData> getConnectionData(
            @PathVariable("doctorId") Long doctorId) throws UnauthorizedUserException {

        ConnectionData body = null;
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (!entitlementValidator.isEntitled(doctorId, Doctor.class)) {
        	status = HttpStatus.BAD_REQUEST;

        } else {
        	Doctor doctor = doctorRepository.findOne(doctorId);

        	body = new ConnectionData();
        	body.setIceEndpoint(turnApiEndpoint + "ice");
        	body.setIdent(turnIdent);
        	body.setDomain(turnDomain);
        	body.setApplication(turnApplicationName);
        	body.setSecret(turnSecret);
        	body.setRoom(doctor.getRoomId());

        	status = HttpStatus.OK;
        	logger.info("[CONNECTION DATA]: Loaded data");
        }

        return new ResponseEntity<>(body, status);
    }

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

				HttpResponse<JsonNode> jsonResponse = Unirest.post(turnApiEndpoint + "room")
						.field("ident", turnIdent)
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

	@PostMapping(value = "{id}/reservation")
	@ResponseStatus(value = HttpStatus.CREATED, reason = "Reservation added.")
	public void addNewReservation(
			@RequestBody ReservationDate reservationDate,
			@PathVariable("id") Long id) {

		Doctor doctor = doctorRepository.findOne(id);
        Reservation reservation = new Reservation(doctor, reservationDate.getStart(), reservationDate.getEnd());
		doctor.addReservation(reservation);
		doctorRepository.save(doctor);
	}

	@GetMapping(value = "{id}/reservations/{type}")
	@ResponseBody
	public List<Reservation> getReservationBasedOnType(@PathVariable("id") Long id, @PathVariable("type") ReservationState reservationState) {
        System.out.println("reservationState: " + reservationState);
        return doctorRepository.findOne(id).getReservations().stream().filter(reservation -> reservation.getReservationState() == reservationState).collect(Collectors.toList());
	}

	@GetMapping(value="{id}/sharedFiles/{sharedFileId}")
	public void getSharedFile(
			@PathVariable("id") Long doctorId,
			@PathVariable("sharedFileId") Long sharedFileId,
			HttpServletResponse response)
					throws UnauthorizedUserException, IOException {

		if (entitlementValidator.isEntitled(doctorId, Doctor.class)) {
			SharedFile sharedFile = sharedFileRepository.findOne(sharedFileId);
			System.out.println("\n\n\n" + sharedFileId + "\n\n\n");

			if (sharedFile == null) {
				throw new UnauthorizedUserException();

			} else {
				Reservation reservation = sharedFile.getReservation();
				LocalDateTime now = LocalDateTime.now();

				if (!doctorId.equals(reservation.getDoctor().getId())) {
					throw new UnauthorizedUserException();

				} else if (now.isBefore(reservation.getStartDateTime())
						|| now.isAfter(reservation.getEndDateTime())) {

					throw new UnauthorizedUserException();

				} else {

					FileEntry fileEntry = sharedFile.getFileEntry();

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
		}
	}

	@GetMapping(value="{id}/reservations/{reservationId}/sharedFiles")
	public List<FileEntry> getSharedFiles(
			@PathVariable("id") Long doctorId,
			@PathVariable("reservationId") Long reservationId) throws UnauthorizedUserException {

		if (entitlementValidator.isEntitled(doctorId, Doctor.class)) {
			Reservation reservation = reservationRepository.findOne(reservationId);

			if (reservation == null) {
				throw new UnauthorizedUserException();

			} else if (!doctorId.equals(reservation.getDoctor().getId())) {
				throw new UnauthorizedUserException();

			}else {
				
				/* Note that we replace the original 
				 * fileEntry ID and URL with ID of the SharedFile
				 * object. We will use it in the /sharedFiles/{sharedFileId} 
				 * endpoint. */

				return sharedFileRepository.findByReservationId(reservationId)
						.stream()
						.map(s -> {
							s.getFileEntry().setId(s.getId());
							s.getFileEntry().setUrl(host + "doctors/" + doctorId + "/sharedFiles/" + s.getId());
							return s.getFileEntry();
						})
						.collect(Collectors.toList());
			}
		}

		/* This line should never be reached but 
		 * the IDE complains about no return statement so
		 * it has to be here anyway. */

		return new ArrayList<>();
	}

	@PostMapping("{id}/available/set/{isAvailable}")
	@ResponseBody
	public boolean setDoctorAvailable(@PathVariable("id") Long id,
			@PathVariable("isAvailable") boolean isAvailable) throws MessagingException, UnauthorizedUserException {

		Doctor doctor = doctorRepository.findOne(id);

		if (doctor != null
				&& entitlementValidator.isEntitled(id, Doctor.class)) {

			doctor.setAvailable(isAvailable);
			doctorRepository.save(doctor);
			logger.info("Doctor [" + id + "] availability status has changed to: "
					+ ("" + doctor.isAvailable()).toUpperCase());
			brokerMessagingTemplate.convertAndSend("/topic/doctors/" + id + "/available", "" + isAvailable);
		}

		return (doctor != null) ? doctor.isAvailable() : false;
	}
}	
