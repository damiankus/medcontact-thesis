package com.medcontact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.CONFLICT, reason="Reservation with given ID has already been taken")
public class ReservationTakenException extends Exception {
	private static final long serialVersionUID = -1394905959202026205L;

}
