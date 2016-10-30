package com.medcontact.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason="Password don't match.")
public class NotMatchedPasswordException extends RuntimeException {
}
