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
	public boolean validate(Patient patient) {
		return userValidator.validate(patient)
				&& isListValid(patient.getFiles(), MAX_FILES_SIZE, fileEntryValidator)
				&& isListValid(patient.getOpinions(), MAX_OPINIONS_SIZE, opinionValidator)
				&& isListValid(patient.getReservations(), MAX_RESERVATIONS_SIZE, reservationValidator);
	}
}
