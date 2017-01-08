package com.medcontact.data.model.dto;

import lombok.Data;

@Data
public class BasicNoteData {
	private Long noteId;
	private String content;
	private Long patientId;
	private Long doctorId;
	
}
