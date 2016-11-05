package com.medcontact.data.model.dto;

import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.domain.Specialty;
import com.medcontact.data.model.enums.Sex;

import lombok.Data;

import java.util.List;

@Data
public class BasicDoctorDetails {
    private final Long id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final Sex sex;
    private final String university;
    private final String biography;
    private final String title;
    private final List<Specialty> specialties;

    public BasicDoctorDetails(Doctor doctor) {
        this.id = doctor.getId();
        this.username = doctor.getUsername();
        this.firstName = doctor.getFirstName();
        this.lastName = doctor.getLastName();
        this.sex = doctor.getSex();
        this.university = doctor.getUniversity();
        this.biography = doctor.getBiography();
        this.title = doctor.getTitle();
        this.specialties = doctor.getSpecialties();
    }
}
