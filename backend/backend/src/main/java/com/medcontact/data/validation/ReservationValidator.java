package com.medcontact.data.validation;

import java.time.LocalDateTime;

import com.medcontact.data.model.domain.Reservation;

public class ReservationValidator extends DataValidatorHelper<Reservation> {
	
	@Override
	public ValidationResult validate(Reservation reservation) {
		ValidationResult result = new ValidationResult();
		
		/* Check if the reservation date and time
		 * are earlier than now. */
		
		if (reservation.getStartDateTime().isAfter(reservation.getEndDateTime())) {
			result.addError("Reservation end time before start time");
			
		} else if (reservation.getStartDateTime().isBefore(LocalDateTime.now())) {
			result.addError("Invalid reservation start time");
		
		} else {
			result.setValid(true);
		}
		
		return result;
	}
}