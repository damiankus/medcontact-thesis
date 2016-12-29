package com.medcontact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND, reason="Item has not been found")
public class ItemNotFoundException extends Exception {
	private static final long serialVersionUID = 8634921427868428612L;
}
