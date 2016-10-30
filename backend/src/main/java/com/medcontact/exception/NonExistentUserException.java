package com.medcontact.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason="The user does not exist.")
public class NonExistentUserException extends RuntimeException {
}
