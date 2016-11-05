package com.medcontact.data.model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BasicReservationData {
	
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private Long doctorId;
	private String doctorName;
	private boolean doctorBusy;
	
	public BasicReservationData() {
		
	}
}
