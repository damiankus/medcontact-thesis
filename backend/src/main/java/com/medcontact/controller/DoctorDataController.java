package com.medcontact.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medcontact.controller.services.DoctorService;
import com.medcontact.data.model.domain.FileEntry;
import com.medcontact.data.model.domain.Note;
import com.medcontact.data.model.domain.Reservation;
import com.medcontact.data.model.dto.BasicDoctorDetails;
import com.medcontact.data.model.dto.BasicNoteDetails;
import com.medcontact.data.model.dto.BasicReservationData;
import com.medcontact.data.model.dto.ConnectionData;
import com.medcontact.data.model.dto.ReservationDate;
import com.medcontact.data.model.enums.ReservationState;
import com.medcontact.exception.UnauthorizedUserException;

@RestController
@RequestMapping("doctors")
public class DoctorDataController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("{id}")
    @ResponseBody
    public BasicDoctorDetails getDoctorInfo(
            @PathVariable("id") Long doctorId) throws IllegalArgumentException {

        return doctorService.getDoctorInfo(doctorId);
    }

    @GetMapping("{doctorId}/connection")
    @ResponseBody
    public ConnectionData getConnectionData(
            @PathVariable("doctorId") Long doctorId) throws UnauthorizedUserException {

        return doctorService.getConnectionData(doctorId);
    }

    @GetMapping(value = "")
    @ResponseBody
    public List<BasicDoctorDetails> getDoctors() {

        return doctorService.getDoctors();
    }

    @PostMapping(value = "{id}/reservation")
    @ResponseStatus(value = HttpStatus.CREATED, reason = "Reservation added.")
    public void addNewReservation(
            @RequestBody ReservationDate reservationDate,
            @PathVariable("id") Long id) {

        doctorService.addNewReservation(id, reservationDate);
    }

    @GetMapping(value = "{id}/reservations/{type}")
    @ResponseBody
    public List<Reservation> getReservationBasedOnType(@PathVariable("id") Long id, @PathVariable("type")
            ReservationState reservationState) {

        return doctorService.getReservationBasedOnType(id, reservationState);
    }

    @GetMapping(value = "{id}/sharedFiles/{sharedFileId}")
    public void getSharedFile(
            @PathVariable("id") Long doctorId,
            @PathVariable("sharedFileId") Long sharedFileId,
            HttpServletResponse response)
            throws UnauthorizedUserException, IOException {

        doctorService.getSharedFile(doctorId, sharedFileId, response);
    }

    @GetMapping(value = "{id}/reservations/{reservationId}/sharedFiles")
    public List<FileEntry> getSharedFiles(
            @PathVariable("id") Long doctorId,
            @PathVariable("reservationId") Long reservationId) throws UnauthorizedUserException {

        return doctorService.getSharedFiles(doctorId, reservationId);
    }

    @GetMapping("{id}/reservations/{reservationId}/next")
    public BasicReservationData getNextReservation(
    		@PathVariable("id") Long doctorId,
    		@PathVariable("reservationId") Long reservationId) throws UnauthorizedUserException {
    	
    	return doctorService.getNextReservation(doctorId, reservationId);
    }
    
    @PostMapping("{id}/notes")
    public void addNote(
    		@PathVariable("id") Long doctorId,
    		@RequestBody BasicNoteDetails noteDetails) throws UnauthorizedUserException {
    	
    	doctorService.addNote(doctorId, noteDetails);
    }
    
    @PutMapping("{id}/notes")
    public void updateNote(
    		@PathVariable("id") Long doctorId,
    		@RequestBody BasicNoteDetails noteDetails) throws UnauthorizedUserException {
    	
    	doctorService.updateNote(doctorId, noteDetails);
    }
    
    @GetMapping("{id}/notes/patient/{patientId}")
    public List<Note> getNotesForPatient(
    		@PathVariable("id") Long doctorId,
    		@PathVariable("patientId") Long patientId) throws UnauthorizedUserException {
    	
    	return doctorService.getNotesForPatient(doctorId, patientId);
    }
    
    @DeleteMapping("{id}/notes/{noteId}")
    public void deleteNote(
    		@PathVariable("id") Long doctorId,
    		@PathVariable("noteId") Long noteId) throws UnauthorizedUserException {
    	
    	doctorService.deleteNote(doctorId, noteId);
    }
}	
