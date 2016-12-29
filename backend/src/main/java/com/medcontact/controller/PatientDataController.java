package com.medcontact.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.medcontact.controller.services.PatientService;
import com.medcontact.data.model.domain.FileEntry;
import com.medcontact.data.model.domain.SharedFile;
import com.medcontact.data.model.dto.BasicReservationData;
import com.medcontact.data.model.dto.BasicUserData;
import com.medcontact.data.model.dto.ConnectionData;
import com.medcontact.data.model.dto.SharedFileDetails;
import com.medcontact.exception.ItemNotFoundException;
import com.medcontact.exception.ReservationNotFoundException;
import com.medcontact.exception.ReservationTakenException;
import com.medcontact.exception.UnauthorizedUserException;
import com.medcontact.exception.UserNotFoundException;

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
            HttpServletResponse response) throws UnauthorizedUserException, IOException {
    	
    	patientService.getFile(patientId, fileId, response);
    }

    @PostMapping(value = "{id}/files")
    public void handleFileUpload(
            @PathVariable("id") Long patientId,
            @RequestParam("files") List<MultipartFile> files)
        throws UnauthorizedUserException, SerialException, SQLException, IOException {

        patientService.handleFileUpload(patientId, files);
    }

    @DeleteMapping(value = "{id}/files/{fileId}")
    public void deleteFile(
    		@PathVariable("id") Long patientId,
    		@PathVariable("fileId") Long fileId)
    				throws UnauthorizedUserException, SerialException, SQLException, IOException, ItemNotFoundException {

        patientService.deleteFile(patientId, fileId);
    }
    
    @PostMapping(value = "{id}/sharedFiles")
    public void shareFile(
            @PathVariable("id") Long patientId,
            @RequestBody SharedFileDetails sharedFileDetails) throws UnauthorizedUserException {

        patientService.shareFile(patientId, sharedFileDetails);
    }
    
    @GetMapping(value = "{id}/sharedFiles")
    public List<SharedFile> getSharedFilesForPatient(
    		@PathVariable("id") Long patientId) throws UnauthorizedUserException, UserNotFoundException {
    	
    	return patientService.getSharedFilesForPatient(patientId);
    }
    
    @DeleteMapping(value = "{id}/sharedFiles/file/{fileId}/reservation/{reservationId}")
    public void cancelShareByFileIdAndReservationId(
    		@PathVariable("id") Long patientId,
    		@PathVariable("fileId") Long fileId,
    		@PathVariable("reservationId") Long reservationId) 
    				throws UnauthorizedUserException, UserNotFoundException, ItemNotFoundException {
    	
    	patientService.cancelShareByFileIdAndReservationId(patientId, fileId, reservationId);
    }

    @GetMapping(value = "{id}/reservations")
    @ResponseBody
    public List<BasicReservationData> getFutureReservations(
            @PathVariable("id") Long patientId) throws UnauthorizedUserException {

        return patientService.getFutureReservations(patientId);
    }

    @PutMapping(value = "{id}/reservations/{reservationId}")
    @ResponseStatus(code=HttpStatus.OK)
    public void bookReservation(
            @PathVariable("id") Long patientId,
            @PathVariable("reservationId") Long reservationId) throws UnauthorizedUserException, ReservationTakenException {
    	
        patientService.bookReservation(patientId, reservationId);
    }
    
    @DeleteMapping(value = "{id}/reservations/{reservationId}")
    @ResponseStatus(code=HttpStatus.OK)
    public void cancelReservation(
            @PathVariable("id") Long patientId,
            @PathVariable("reservationId") Long reservationId) throws UnauthorizedUserException, ReservationNotFoundException {

        patientService.cancelReservation(patientId, reservationId);
    }

    @PutMapping(value = "{id}")
    @ResponseBody
    public void changePersonalData(
            @PathVariable("id") Long patientId,
            @RequestBody BasicUserData patientData) throws UnauthorizedUserException {

        patientService.changePersonalData(patientId, patientData);
    }
}
