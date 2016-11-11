package com.medcontact.data.model.dto;

import lombok.Data;

@Data
public class PersonalDataPassword {
    private String firstName;
    private String lastName;
    private String email;
    private String oldPassword;
    private String newPassword1;
    private String newPassword2;
}
