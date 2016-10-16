package com.medcontact.data.validation;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import com.medcontact.data.model.Reservation;

public class ReservationValidator extends DataValidatorHelper<Reservation> {
	
	@Override
	public boolean validate(Reservation reservation) {
		return (reservation.getStartTime().getTime() < 
					reservation.getEndTime().getTime())
				
				/* Check if the reservation date and time
				 * are earlier than now. */
				
				&& !reservation.getStartTime().before(Time.valueOf(LocalTime.now()))
				&& !reservation.getDate().before(Date.valueOf(LocalDate.now()));
	}
}