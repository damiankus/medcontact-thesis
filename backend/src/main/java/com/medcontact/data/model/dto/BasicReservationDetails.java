package com.medcontact.data.model.dto;

import java.time.LocalDateTime;

import com.medcontact.data.model.domain.Reservation;

import lombok.Data;

@Data
public class BasicReservationDetails {
	
	private Long id;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private Long doctorId;
	private String doctorName;
	private boolean doctorBusy;
	
	public BasicReservationDetails() {
		
	}
	
	public BasicReservationDetails(Reservation reservation) {
		this.setDoctorId(reservation.getDoctor().getId());
    	this.setDoctorName(reservation.getDoctor().getFirstName()
    			+ " " + reservation.getDoctor().getLastName());
    	this.setDoctorBusy(reservation.getDoctor().isBusy());
    	this.setStartDateTime(reservation.getStartDateTime());
    	this.setEndDateTime(reservation.getEndDateTime());
	}
}
