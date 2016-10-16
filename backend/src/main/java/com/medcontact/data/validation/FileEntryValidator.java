package com.medcontact.data.validation;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.medcontact.data.model.FileEntry;

public class FileEntryValidator extends DataValidatorHelper<FileEntry> {
	public int MAX_DESCRIPTION_LEN = 128;
	
	@Override 
	public boolean validate(FileEntry fileEntry) {
		return !fileEntry.getUploadTime().after(Timestamp.valueOf(LocalDateTime.now()))
				&& isStringLengthValid(fileEntry.getName(), MAX_DESCRIPTION_LEN);
	}
}
