package com.medcontact.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import com.medcontact.controller.services.PatientService;
import com.medcontact.data.model.domain.FileEntry;
import com.medcontact.data.model.dto.BasicReservationData;
import com.medcontact.data.model.dto.ConnectionData;
import com.medcontact.data.model.dto.PersonalDataPassword;
import com.medcontact.data.model.dto.SharedFileDetails;
import com.medcontact.exception.UnauthorizedUserException;

@RestController
@RequestMapping(value = "patients")
public class PatientDataController {
    @Autowired
    private PatientService patientService;

    @GetMapping("{patientId}/connection/{reservationId}")
    public ResponseEntity<ConnectionData> getConnectionData(
            @PathVariable("patientId") Long patientId,
            @PathVariable("reservationId") Long reservationId) {

        return patientService.getConnectionData(patientId, reservationId);
    }

    @GetMapping(value = "{id}/fileEntries")
    public List<FileEntry> getFileEntries(
            @PathVariable("id") Long patientId) throws UnauthorizedUserException {
        return patientService.getFileEntries(patientId);
    }
    
    @GetMapping(value = "{id}/files/{fileId}")
    public void getFile(
            @PathVariable("id") Long patientId,
            @PathVariable("fileId") Long fileId,
            HttpServletResponse response)
            		throws UnauthorizedUserException, IOException {
    	
    	patientService.getFile(patientId, fileId, response);
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
    	
    	System.out.println("BOOKING");
        patientService.bookReservation(patientId, reservationId);
    }

    @PutMapping(value = "{id}")
    @ResponseBody
    public void changePersonalData(
            @PathVariable("id") Long patientId,
            @RequestBody PersonalDataPassword personalDataPassword) {

        patientService.changePersonalData(patientId, personalDataPassword);
    }
}
