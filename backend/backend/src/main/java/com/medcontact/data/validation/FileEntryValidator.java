package com.medcontact.data.validation;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.medcontact.data.model.domain.FileEntry;

public class FileEntryValidator extends DataValidatorHelper<FileEntry> {
	public int MAX_DESCRIPTION_LEN = 128;
	
	@Override 
	public ValidationResult validate(FileEntry fileEntry) {
		ValidationResult result = new ValidationResult();
		
		if (fileEntry.getUploadTime().after(Timestamp.valueOf(LocalDateTime.now()))) {
			result.addError("Invalid upload time");
			
		} else if (!isStringLengthValid(fileEntry.getName(), MAX_DESCRIPTION_LEN)) {
			result.addError("Invalid file name length");
			
		} else {
			result.setValid(true);
		}
		
		return result;
	}
}
