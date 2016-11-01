package com.medcontact.data.validation;

import com.medcontact.data.model.Patient;

public class PatientValidator extends DataValidatorHelper<Patient> {
	public final int MAX_FILES_SIZE = Integer.MAX_VALUE;
	public final int MAX_OPINIONS_SIZE = Integer.MAX_VALUE;
	public final int MAX_RESERVATIONS_SIZE = Integer.MAX_VALUE;
	
	private BasicUserValidator userValidator = new BasicUserValidator();
	private FileEntryValidator fileEntryValidator = new FileEntryValidator();
	private OpinionValidator opinionValidator = new OpinionValidator();
	private ReservationValidator reservationValidator = new ReservationValidator();
	
	@Override
	public ValidationResult validate(Patient patient) {
		ValidationResult result = new ValidationResult();
		ValidationResult partialResult = userValidator.validate(patient);
		
		if (!partialResult.isValid()) {
			result.addManyErrors(partialResult.getErrors());
		} else {
			partialResult = isListValid(patient.getOpinions(), "opinions", 
					MAX_OPINIONS_SIZE, opinionValidator);
			
			if (!partialResult.isValid()) {
				result.addManyErrors(partialResult.getErrors());
			} else {
				partialResult = isListValid(patient.getReservations(), "reservations", 
						MAX_RESERVATIONS_SIZE, reservationValidator);
				
				if (!partialResult.isValid()) {
					result.addManyErrors(partialResult.getErrors());
				} else {
					partialResult = isListValid(patient.getFileEntries(), "files", 
							MAX_FILES_SIZE, fileEntryValidator);
					
					if (!partialResult.isValid()) {
						result.addManyErrors(partialResult.getErrors());
						
					} else {
						result.setValid(true);
					}
				}
			}
		}
		
		return result;
	}
	
}
