package com.medcontact.data.validation;

import com.medcontact.data.model.domain.Doctor;

public class DoctorValidator extends DataValidatorHelper<Doctor> {
	public final int MAX_UNIVERSITY_LEN = 128;
	public final int MAX_BIOGRAPHY_LEN = 2048;
	public final int MAX_TITLE_LEN = 128;

	public final int MAX_SPECIALTIES_SIZE = Integer.MAX_VALUE;
	public final int MAX_OPINIONS_SIZE = Integer.MAX_VALUE;
	public final int MAX_RESERVATIONS_SIZE = Integer.MAX_VALUE;
	public final int MAX_NOTES_SIZE = Integer.MAX_VALUE;

	private BasicUserValidator userValidator = new BasicUserValidator();
	private NoteValidator noteValidator = new NoteValidator();
	private OpinionValidator opinionValidator = new OpinionValidator();
	private ReservationValidator reservationValidator = new ReservationValidator();

	@Override
	public ValidationResult validate(Doctor doctor) {
		ValidationResult result = new ValidationResult();
		ValidationResult partialResult;

		partialResult = userValidator.validate(doctor);

		if (!partialResult.isValid()) {
			result.addManyErrors(partialResult.getErrors());
		} else {
			if (!isStringLengthValid(doctor.getUniversity(), MAX_UNIVERSITY_LEN)) {
				result.addError("Nazwa uczelni ma niewłaściwą długość");

			} else if (!isStringLengthValid(doctor.getBiography(), MAX_BIOGRAPHY_LEN)) {
				result.addError("Biografia ma niewłaściwą długość");

			} else if (!isStringLengthValid(doctor.getTitle(), MAX_TITLE_LEN)) {
				result.addError("Tytuł naukowy ma niewłaściwą długość");

			} else {
				partialResult = isListValid(doctor.getNotes(), "notes", MAX_NOTES_SIZE, noteValidator);

				if (!partialResult.isValid()) {
					result.addManyErrors(partialResult.getErrors());

				} else {
					partialResult = isListValid(doctor.getNotes(), "notes", MAX_NOTES_SIZE, noteValidator);

					if (!partialResult.isValid()) {
						result.addManyErrors(partialResult.getErrors());

					} else {
						partialResult = isListValid(doctor.getOpinions(), "opinions", MAX_OPINIONS_SIZE, opinionValidator);

						if (!partialResult.isValid()) {
							result.addManyErrors(partialResult.getErrors());

						} else {
							partialResult = isListValid(doctor.getReservations(), "reservations",  MAX_RESERVATIONS_SIZE, reservationValidator);

							if (!partialResult.isValid()) {
                                result.addManyErrors(partialResult.getErrors());


                                if (!partialResult.isValid()) {
                                    result.addManyErrors(partialResult.getErrors());

                                } else {
                                    result.setValid(true);
                                }
                            }
						}
					}
				}
			}
		}

		return result;
	}
}
