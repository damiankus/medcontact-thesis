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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;

import com.medcontact.controller.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.domain.FileEntry;
import com.medcontact.data.model.domain.Patient;
import com.medcontact.data.model.domain.Reservation;
import com.medcontact.data.model.domain.SharedFile;
import com.medcontact.data.model.dto.BasicReservationData;
import com.medcontact.data.model.dto.ConnectionData;
import com.medcontact.data.model.dto.PersonalDataPassword;
import com.medcontact.data.model.dto.SharedFileDetails;
import com.medcontact.data.model.dto.UserFilename;
import com.medcontact.data.model.enums.ReservationState;
import com.medcontact.data.repository.FileRepository;
import com.medcontact.data.repository.PatientRepository;
import com.medcontact.data.repository.ReservationRepository;
import com.medcontact.data.repository.SharedFileRepository;
import com.medcontact.exception.UnauthorizedUserException;
import com.medcontact.security.config.EntitlementValidator;

import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping(value = "patients")
public class PatientAccountController {
    @Autowired
    private PatientService patientService;

    @GetMapping("{patientId}/connection/{consultationId}")
    public ResponseEntity<ConnectionData> getConnectionData(
            @PathVariable("patientId") Long patientId,
            @PathVariable("consultationId") Long reservationId) {

        return patientService.getConnectionData(patientId, reservationId);
    }

    @GetMapping(value = "{id}/fileEntries")
    public List<FileEntry> getFileEntries(
            @PathVariable("id") Long patientId) throws UnauthorizedUserException {
        return patientService.getFileEntries(patientId);
    }

    @PostMapping(value = "{id}/files")
    public void handleFileUpload(
            @PathVariable("id") Long patientId,
            @RequestParam("files") List<MultipartFile> files)
            throws UnauthorizedUserException, SerialException, SQLException, IOException {

        patientService.handleFileUpload(patientId, files);
    }

    @PostMapping(value = "{id}/sharedFiles")
    public void shareFile(
            @PathVariable("id") Long patientId,
            @RequestBody SharedFileDetails sharedFileDetails) throws UnauthorizedUserException {

        patientService.shareFile(patientId, sharedFileDetails);
    }

    @GetMapping(value = "{id}/reservations")
    @ResponseBody
    public List<BasicReservationData> getCurrentReservations(
            @PathVariable("id") Long patientId) throws UnauthorizedUserException {

        return patientService.getCurrentReservations(patientId);
    }

    @PutMapping(value = "{id}/reservations/{reservation_id}")
    @ResponseBody
    public void bookReservation(
            @PathVariable("id") Long patientId,
            @PathVariable("reservation_id") Long reservationId) {

        patientService.bookReservation(patientId, reservationId);
    }

    @PostMapping(value = "{id}/personal-data")
    @ResponseBody
    public void changePersonalData(
            @PathVariable("id") Long patientId,
            @RequestBody PersonalDataPassword personalDataPassword) {

        patientService.changePersonalData(patientId, personalDataPassword);
    }
}
