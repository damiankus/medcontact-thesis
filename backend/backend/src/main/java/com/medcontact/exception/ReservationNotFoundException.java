package com.medcontact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.BAD_REQUEST, reason="Reservation with given ID hasn't been found")
public class ReservationNotFoundException extends Exception {

	private static final long serialVersionUID = -790649990385170845L;

}
