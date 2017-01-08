package com.medcontact.data.model.dto;

import com.medcontact.data.model.domain.SharedFile;

import lombok.Data;

@Data
public class BasicSharedFileData {
	private long id;
	private long fileEntryId;
	private long reservationId;
	
	public BasicSharedFileData(SharedFile sharedFile) {
		this.id = sharedFile.getId();
		this.fileEntryId = sharedFile.getFileEntry().getId();
		this.reservationId = sharedFile.getReservation().getId();
	}
}
