package com.medcontact.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason="Password don't match.")
public class NotMatchedPasswordException extends RuntimeException {
	private static final long serialVersionUID = 4528496269719903648L;
}
