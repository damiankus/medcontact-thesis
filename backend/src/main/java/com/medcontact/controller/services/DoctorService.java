package com.medcontact.controller.services;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.medcontact.data.model.domain.*;
import com.medcontact.data.model.dto.*;
import com.medcontact.exception.NotMatchedPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.medcontact.controller.DoctorDataController;
import com.medcontact.data.model.enums.ReservationState;
import com.medcontact.data.repository.DoctorRepository;
import com.medcontact.data.repository.NoteRepository;
import com.medcontact.data.repository.PatientRepository;
import com.medcontact.data.repository.ReservationRepository;
import com.medcontact.data.repository.SharedFileRepository;
import com.medcontact.exception.NonExistentUserException;
import com.medcontact.exception.UnauthorizedUserException;
import com.medcontact.security.config.EntitlementValidator;

import lombok.Getter;
import lombok.Setter;

@Service
public class DoctorService {
    private Logger logger = Logger.getLogger(DoctorDataController.class.getName());

    @Getter
    @Setter
    @Value("${backend.host}")
    private String host;

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private NoteRepository noteRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SharedFileRepository sharedFileRepository;

    @Autowired
    private EntitlementValidator entitlementValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public BasicDoctorData getDoctorInfo(Long doctorId) {
        Doctor doctor = doctorRepository.findOne(doctorId);

        if (doctor == null) {
            throw new IllegalArgumentException();

        } else {
            return new BasicDoctorData(doctor);
        }
    }


    public ConnectionData getConnectionData(Long doctorId) throws UnauthorizedUserException {
        if (!entitlementValidator.isEntitled(doctorId, Doctor.class)) {
            throw new UnauthorizedUserException();
        }
        Doctor doctor = doctorRepository.findOne(doctorId);
        ConnectionData connectionData = new ConnectionData(turnApiEndpoint + "ice", turnIdent, turnDomain,
                turnApplicationName, turnSecret, doctor.getRoomId());
        logger.info("[CONNECTION DATA]: Loaded data");

        return connectionData;
    }

    public List<BasicDoctorData> getDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(BasicDoctorData::new)
                .collect(Collectors.toList());
    }

    public void addNewReservation(Long id, ReservationDate reservationDate) {
        Doctor doctor = doctorRepository.findOne(id);
        Reservation reservation = new Reservation(doctor, reservationDate.getStart(), reservationDate.getEnd());
        doctor.getReservations().add(reservation);
        doctorRepository.save(doctor);
    }

    public List<Reservation> getReservationBasedOnType(Long id, ReservationState reservationState) {
        return doctorRepository
                .findOne(id)
                .getReservations()
                .stream()
                .filter(reservation -> reservation.getReservationState() == reservationState)
                .collect(Collectors.toList());
    }

    public void getSharedFile(Long doctorId, Long sharedFileId, HttpServletResponse response)
            throws IOException, UnauthorizedUserException {
        if (entitlementValidator.isEntitled(doctorId, Doctor.class)) {
            SharedFile sharedFile = sharedFileRepository.findOne(sharedFileId);

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
                                    Paths.get(fileEntry.getPath()), out);
                        }
                    }
                }
            }
        }
    }

    public List<FileEntry> getSharedFiles(Long doctorId, Long reservationId) throws UnauthorizedUserException {
        if (!entitlementValidator.isEntitled(doctorId, Doctor.class)) {
            throw new UnauthorizedUserException();
        }

        Reservation reservation = reservationRepository.findOne(reservationId);
        if (reservation == null || !doctorId.equals(reservation.getDoctor().getId())) {
            throw new UnauthorizedUserException();
        }

        return sharedFileRepository.findByReservationId(reservationId)
                .stream()
                .map(s -> {
                    s.getFileEntry().setId(s.getId());
                    s.getFileEntry().setUrl(host + "/doctors/" + doctorId + "/sharedFiles/" + s.getId());
                    return s.getFileEntry();
                })
                .collect(Collectors.toList());
    }

    public boolean setDoctorAvailable(Long doctorId, boolean isAvailable) throws UnauthorizedUserException {
        Doctor doctor = doctorRepository.findOne(doctorId);
        if (doctor == null) {
            throw new NonExistentUserException();
        }
        if (!entitlementValidator.isEntitled(doctorId, Doctor.class)){
            throw new UnauthorizedUserException();
        }
        
        doctor.setAvailable(isAvailable);
        doctorRepository.save(doctor);
        logger.info("Doctor [" + doctorId + "] availability status has changed to: "
                + ("" + doctor.isAvailable()).toUpperCase());
        brokerMessagingTemplate.convertAndSend("/topic/doctors/" + doctorId + "/available", "" + isAvailable);

        return doctor.isAvailable();
    }

    
    public BasicReservationData getNextReservation(Long doctorId, Long reservationId) throws UnauthorizedUserException {
    	if (!entitlementValidator.isEntitled(doctorId, Doctor.class)){
            throw new UnauthorizedUserException();
        } 
    	
    	Reservation reservation = reservationRepository.findOne(reservationId);
    	
    	if (reservation == null) {
    		throw new UnauthorizedUserException();
    		
    	} else if (!reservation.getDoctor().getId().equals(doctorId)) {
    		throw new UnauthorizedUserException();
    	}
    	
    	List<BasicReservationData> nextReservations = reservationRepository.findNextReservations(reservation.getStartDateTime())
    			.stream()
    			.filter(r -> r.getReservationState() == ReservationState.RESERVED)
    			.limit(1)
    			.map(r -> new BasicReservationData(r))
    			.collect(Collectors.toList());
    	
    	return (nextReservations.size() > 0) ? nextReservations.get(0) : new BasicReservationData();
    }
    
    public void addNote(Long doctorId, BasicNoteData noteDetails) throws UnauthorizedUserException {
    	entitlementValidator.isEntitled(doctorId, Doctor.class);
    	
    	Doctor doctor = doctorRepository.findOne(doctorId);
    	Patient patient = patientRepository.findOne(noteDetails.getPatientId());
    	
    	if (doctor == null) {
    		throw new UnauthorizedUserException();
    	} else if (!doctor.getId().equals(doctorId)) {
    		throw new UnauthorizedUserException();
    	} else if (patient == null) {
    		throw new UnauthorizedUserException();
    	} else {
    		Note note = new Note();
    		note.setContent(noteDetails.getContent());
    		note.setPatient(patient);
    		note.setDoctor(doctor);
    		doctor.getNotes().add(note);
    		
    		doctorRepository.save(doctor);
    	}
    }
    
    public void updateNote(Long doctorId, BasicNoteData noteDetails) throws UnauthorizedUserException {
    	entitlementValidator.isEntitled(doctorId, Doctor.class);
    	Doctor doctor = doctorRepository.findOne(doctorId);
    	
    	if (doctor == null) {
    		throw new UnauthorizedUserException();
    		
    	} else {
    		Note note = noteRepository.findOne(noteDetails.getNoteId());
    		
    		if (note == null) {
        		throw new UnauthorizedUserException();
        		
    		} else if (!note.getDoctor().getId().equals(doctorId)) {
        		throw new UnauthorizedUserException();
        		
    		} else {
    			note.setContent(noteDetails.getContent());
    			noteRepository.save(note);
    		}
    	}
    }
    
    public void deleteNote(Long doctorId, Long noteId) throws UnauthorizedUserException {
    	entitlementValidator.isEntitled(doctorId, Doctor.class);
    	
    	Doctor doctor = doctorRepository.findOne(doctorId);
    	
    	if (doctor == null) {
    		throw new UnauthorizedUserException();
    	} else {
    		Optional<Note> noteOptional = doctor.getNotes()
    				.stream()
    				.filter(n -> n.getId() == noteId)
    				.findFirst();
    		
    		if (!noteOptional.isPresent()) {
        		throw new UnauthorizedUserException();
        		
    		} else {
    			doctor.getNotes().removeIf(n -> n.getId() == noteId);
    			doctorRepository.save(doctor);
    			noteRepository.delete(noteId);
    		}
    	}
    }
    
    public List<Note> getNotesForPatient(Long doctorId, Long patientId) throws UnauthorizedUserException {
    	entitlementValidator.isEntitled(doctorId, Doctor.class);
    	
    	Doctor doctor = doctorRepository.findOne(doctorId);
    	
    	if (doctor == null) {
    		throw new UnauthorizedUserException();
    	}
    	
    	return doctor.getNotes().stream()
    			.filter(n -> n.getPatient().getId() == patientId)
    			.collect(Collectors.toList());
    }

    public void changePersonalData(Long doctorId, PersonalDataPassword personalDataPassword) {
        Doctor doctor = doctorRepository.findOne(doctorId);

        doctor.changePersonalData(personalDataPassword, passwordEncoder);
        if(personalDataPassword.getNewPassword1() != null && personalDataPassword.getNewPassword1().equals(personalDataPassword.getNewPassword2())){
            if (passwordEncoder.matches(personalDataPassword.getOldPassword(), doctor.getPassword())){
                doctor.setPassword(passwordEncoder.encode(personalDataPassword.getNewPassword1()));
            }
            else{
                throw new NotMatchedPasswordException();
            }
        }
        doctorRepository.save(doctor);
    }
}
