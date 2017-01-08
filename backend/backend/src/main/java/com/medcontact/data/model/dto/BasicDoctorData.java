package com.medcontact.data.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.enums.ApplicationRole;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)

public class BasicDoctorData extends BasicUserData {
    private String university;
    private String biography;
    private String title;
    private List<String> specialties;

    public BasicDoctorData() {
    	super();
    	this.title = "";
    	this.university = "";
    	this.biography = "";
    	this.specialties = new ArrayList<>();
    	this.role = ApplicationRole.DOCTOR;
    }
    
    public BasicDoctorData(Doctor doctor) {
        this.id = doctor.getId();
        this.email = doctor.getEmail();
        this.username = doctor.getUsername();
        this.firstName = doctor.getFirstName();
        this.lastName = doctor.getLastName();
        this.university = doctor.getUniversity();
        this.biography = doctor.getBiography();
        this.title = doctor.getTitle();
        this.specialties = doctor.getSpecialties()
        		.stream()
        		.map(s -> s.getName())
        		.collect(Collectors.toList());

    }
}
