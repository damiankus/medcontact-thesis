package com.medcontact.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason="The specified email has been taken.")
public class EmailTakenException extends RuntimeException {
	private static final long serialVersionUID = -4650205840400149871L;

	public EmailTakenException(String email) {
        super("Email: {" + email + "} has been taken.");
    }
}
