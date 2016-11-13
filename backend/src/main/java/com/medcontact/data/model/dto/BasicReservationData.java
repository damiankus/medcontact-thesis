package com.medcontact.data.model.dto;

import java.time.LocalDateTime;

import com.medcontact.data.model.domain.Reservation;
import lombok.Data;

@Data
public class BasicReservationData {
	private long id;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private Long doctorId;
	private String doctorName;
	private boolean doctorAvailable;
	
	public BasicReservationData(Reservation reservation) {
		id = reservation.getId();
		doctorId = reservation.getDoctor().getId();
		doctorName = reservation.getDoctor().getFirstName() + " " + reservation.getDoctor().getLastName();
		doctorAvailable = reservation.getDoctor().isAvailable();
		startDateTime = reservation.getStartDateTime();
		endDateTime = reservation.getEndDateTime();
	}
}
