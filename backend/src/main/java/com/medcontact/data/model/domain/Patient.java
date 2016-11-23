package com.medcontact.data.model.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.crypto.password.PasswordEncoder;

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
@ToString(exclude={"fileEntries", "reservations", "opinions"})
public class Patient extends BasicUser {
	private static final long serialVersionUID = -6160436846217117334L;

	@OneToMany(mappedBy="fileOwner", fetch=FetchType.LAZY, cascade= CascadeType.ALL)
	@JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
	private List<FileEntry> fileEntries;

	@OneToMany(mappedBy="patient", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
	private List<Reservation> reservations;

	@OneToMany(mappedBy="ratingPatient", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
	private List<Opinion> opinions;

	/* Setting default values for the members */

	public Patient() {
		super();
		this.fileEntries = new ArrayList<>();
		this.opinions = new ArrayList<>();
		this.reservations = new ArrayList<>();
		this.role = ApplicationRole.PATIENT;
	}

	public static PatientBuilder getBuilder() {
		return new PatientBuilder();
	}

	public void changePersonalData(PersonalDataPassword personalDataPassword, PasswordEncoder passwordEncoder) {
		this.firstName = personalDataPassword.getFirstName();
		this.lastName = personalDataPassword.getLastName();
		this.email = personalDataPassword.getEmail();

		if(passwordEncoder.matches(personalDataPassword.getOldPassword(), this.password) &&
				personalDataPassword.getNewPassword1().equals(personalDataPassword.getNewPassword2()))
			this.password = passwordEncoder.encode(personalDataPassword.getNewPassword1());
	}

	public void addReservatin(Reservation reservation) {
		this.reservations.add(reservation);
	}

    public static class PatientBuilder extends BasicUser.BasicUserBuilder {
		public PatientBuilder() {
			this.user = new Patient();
		}

		public PatientBuilder setFiles(List<FileEntry> fileEntries) {
			((Patient) user).setFileEntries(fileEntries);
			return this;
		}

		public PatientBuilder setOpinions(List<Opinion> opinions) {
			((Patient) user).setOpinions(opinions);
			return this;
		}

		public PatientBuilder setReservations(List<Reservation> reservations) {
			((Patient) user).setReservations(reservations);
			return this;
		}
	}
}
