package com.medcontact.data.model.dto;

import java.time.LocalDateTime;

import com.medcontact.data.model.domain.Reservation;
import lombok.Data;

@Data
public class BasicReservationData {
	private long id;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private Long patientId;
	private String patientName;
	private Long doctorId;
	private String doctorName;
	
	public BasicReservationData() {
		this.id = 0;
		this.patientId = 0L;
		this.patientName = "";
		this.doctorId = 0L;
		this.doctorName = "";
		this.startDateTime = LocalDateTime.MIN;
		this.endDateTime = LocalDateTime.MIN;
	}
	
	public BasicReservationData(Reservation reservation) {
		this.id = reservation.getId();
		this.patientId = reservation.getPatient().getId();
		this.patientName = reservation.getPatient().getFirstName() + " " + reservation.getPatient().getLastName();
		this.doctorId = reservation.getDoctor().getId();
		this.doctorName = reservation.getDoctor().getFirstName() + " " + reservation.getDoctor().getLastName();
		this.startDateTime = reservation.getStartDateTime();
		this.endDateTime = reservation.getEndDateTime();
	}
}
