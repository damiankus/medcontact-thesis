package com.medcontact.data.model.dto;

import java.util.List;

import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.domain.Specialty;

import lombok.Data;

@Data
public class BasicDoctorData {
    private final Long id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String university;
    private final String biography;
    private final String title;
    private final List<Specialty> specialties;

    public BasicDoctorData(Doctor doctor) {
        this.id = doctor.getId();
        this.username = doctor.getUsername();
        this.firstName = doctor.getFirstName();
        this.lastName = doctor.getLastName();
        this.university = doctor.getUniversity();
        this.biography = doctor.getBiography();
        this.title = doctor.getTitle();
        this.specialties = doctor.getSpecialties();

    }
}
