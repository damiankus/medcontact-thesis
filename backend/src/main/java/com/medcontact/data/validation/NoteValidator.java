package com.medcontact.data.validation;

import com.medcontact.data.model.domain.Note;

public class NoteValidator extends DataValidatorHelper<Note> {
	public final int MAX_TITLE_LEN = 128;
	public final int MAX_CONTENT_LEN = 2048;
	
	@Override
	public ValidationResult validate(Note note) {
		ValidationResult result = new ValidationResult();
		
		if (!isStringLengthValid(note.getContent(), MAX_CONTENT_LEN)) {
			result.addError("Invalid content length");
			
		} else {
			result.setValid(true);
		}
		
		return result;
	}
}
