package com.medcontact.data.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.domain.Specialty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)

public class BasicDoctorData extends BasicUserData {
    private String university;
    private String biography;
    private String title;
    private List<Specialty> specialties;

    public BasicDoctorData() {
    	super();
    	this.title = "";
    	this.university = "";
    	this.biography = "";
    	this.specialties = new ArrayList<>();
    }
    
    public BasicDoctorData(Doctor doctor) {
        this.id = doctor.getId();
        this.firstName = doctor.getFirstName();
        this.lastName = doctor.getLastName();
        this.university = doctor.getUniversity();
        this.biography = doctor.getBiography();
        this.title = doctor.getTitle();
        this.specialties = doctor.getSpecialties();

    }
}
