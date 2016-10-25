package com.medcontact.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason="The specified email has been taken.")
public class EmailTakenException extends RuntimeException {

    public EmailTakenException(String email) {
        super("Email: {" + email + "} has been taken.");
    }
}
