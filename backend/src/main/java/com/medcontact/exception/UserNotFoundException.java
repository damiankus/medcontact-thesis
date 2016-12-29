package com.medcontact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND, reason="User with specified ID has not been found")
public class UserNotFoundException extends Exception {
	private static final long serialVersionUID = 7965291386578928821L;
}
