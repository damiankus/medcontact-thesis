package com.medcontact.data.model.dto;

import java.time.LocalDateTime;

import com.medcontact.data.model.domain.Reservation;
import lombok.Data;

@Data
public class BasicReservationData {
	
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private Long doctorId;
	private String doctorName;
	private boolean doctorBusy;
	
	public BasicReservationData(Reservation reservation) {
		doctorId = reservation.getDoctor().getId();
		doctorName = reservation.getDoctor().getFirstName() + " " + reservation.getDoctor().getLastName();
		doctorBusy = reservation.getDoctor().isBusy();
		startDateTime = reservation.getStartDateTime();
		endDateTime = reservation.getEndDateTime();
	}
}
