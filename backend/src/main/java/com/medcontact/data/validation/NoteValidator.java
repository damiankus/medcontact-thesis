package com.medcontact.data.validation;

import com.medcontact.data.model.Note;

public class NoteValidator extends DataValidatorHelper<Note> {
	public final int MAX_TITLE_LEN = 128;
	public final int MAX_CONTENT_LEN = 2048;
	
	@Override
	public boolean validate(Note note) {
		return isStringLengthValid(note.getTitle(), MAX_TITLE_LEN)
				&& isStringLengthValid(note.getContent(), MAX_CONTENT_LEN);
	}
}
