package com.medcontact.data.validation;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import com.medcontact.data.model.domain.Reservation;

public class ReservationValidator extends DataValidatorHelper<Reservation> {
	
	@Override
	public ValidationResult validate(Reservation reservation) {
		ValidationResult result = new ValidationResult();
		
		/* Check if the reservation date and time
		 * are earlier than now. */
		
		if (reservation.getStartTime().getTime()
				>= reservation.getEndTime().getTime()) {
			result.addError("Reservation end time before start time");
			
		} else if (reservation.getStartTime().before(Time.valueOf(LocalTime.now()))) {
			result.addError("Invalid reservation start time");
		
		} else if (reservation.getDate().before(Date.valueOf(LocalDate.now()))) {
			result.addError("Invalid reservation date");
		} else {
			result.setValid(true);
		}
		
		return result;
	}
}