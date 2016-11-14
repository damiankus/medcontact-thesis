package com.medcontact.data.model.dto;

import lombok.Data;

@Data
public class DoctorCreateData {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String password;
}
