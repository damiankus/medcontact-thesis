package com.medcontact.data.model.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.medcontact.data.model.builders.PatientBuilder;
import com.medcontact.data.model.dto.PersonalDataPassword;
import com.medcontact.data.model.enums.ApplicationRole;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name="patients")
@DiscriminatorValue("PATIENT")
@Data

/* This annotations prevents the lombok library
 * from calling the superclass' equals and hashCode 
 * methods thus preventing warnings about
 * overriding the mentioned methods. */

@EqualsAndHashCode(callSuper=false)
@ToString(exclude={"fileEntries", "reservations"})
public class Patient extends BasicUser {
	private static final long serialVersionUID = -6160436846217117334L;

    @OneToMany(mappedBy="fileOwner", fetch=FetchType.LAZY, cascade= CascadeType.ALL)
    @JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
    private List<FileEntry> fileEntries;

    @OneToMany(mappedBy="patient", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
    private List<Reservation> reservations;

	/* Setting default values for the members */

	public Patient() {
		super();
		this.fileEntries = new ArrayList<>();
		this.reservations = new ArrayList<>();
		this.role = ApplicationRole.PATIENT;
	}

	public static PatientBuilder getBuilder() {
		return new PatientBuilder();
	}

	public void addReservation(Reservation reservation) {
		this.reservations.add(reservation);
	}

}
