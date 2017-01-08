package com.medcontact.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason="The user does not exist.")
public class NonExistentUserException extends RuntimeException {
	private static final long serialVersionUID = -4615906718759404660L;
}
