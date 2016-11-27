package com.medcontact.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.medcontact.controller.services.DoctorService;
import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.domain.FileEntry;
import com.medcontact.data.model.domain.Reservation;
import com.medcontact.data.model.dto.BasicDoctorDetails;
import com.medcontact.data.model.dto.BasicReservationData;
import com.medcontact.data.model.dto.ConnectionData;
import com.medcontact.data.model.dto.ReservationDate;
import com.medcontact.data.model.enums.ReservationState;
import com.medcontact.exception.UnauthorizedUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("doctors")
public class DoctorDataController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("{id}/info")
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

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> addDoctor(
            @RequestBody Doctor doctor) throws UnirestException {

        return doctorService.addDoctor(doctor);
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

    @PostMapping("{id}/available/set/{isAvailable}")
    @ResponseBody
    public boolean setDoctorAvailable(@PathVariable("id") Long id,
                                      @PathVariable("isAvailable") boolean isAvailable) throws MessagingException,
            UnauthorizedUserException {

        return doctorService.setDoctorAvailable(id, isAvailable);
    }
    
    @GetMapping("{id}/reservations/{reservationId}/next")
    public BasicReservationData getNextReservation(
    		@PathVariable("id") Long doctorId,
    		@PathVariable("reservationId") Long reservationId) throws UnauthorizedUserException {
    	
    	return doctorService.getNextReservation(doctorId, reservationId);
    }
}	
