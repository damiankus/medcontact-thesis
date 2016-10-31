package com.medcontact.data.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="patients")
@DiscriminatorValue("PATIENT")
@Data

/* This annotations prevents the lombok library
 * from calling the superclass' equals and hashCode 
 * methods thus preventing warnings about
 * overriding the mentioned methods. */

@EqualsAndHashCode(callSuper=false)

public class Patient extends BasicUser {
	private static final long serialVersionUID = -6160436846217117334L;
	
	@OneToMany(mappedBy="fileOwner", fetch=FetchType.LAZY)
	@JsonIgnore
	private List<FileEntry> files;
	
	@OneToMany(mappedBy="patient", fetch=FetchType.EAGER)
	private List<Reservation> reservations;
	
	@OneToMany(mappedBy="ratingPatient", fetch=FetchType.LAZY)
	@JsonIgnore
	private List<Opinion> opinions;
	
	/* Setting default values for the members */
	
	public Patient() {
		super();
		this.files = new ArrayList<>();
		this.opinions = new ArrayList<>();
		this.reservations = new ArrayList<>();
		this.role = ApplicationRole.PATIENT;
	}
	
	public static PatientBuilder getBuilder() {
		return new PatientBuilder();
	}
	
	public static class PatientBuilder extends BasicUser.BasicUserBuilder {
		public PatientBuilder() {
			this.user = new Patient();
		}
		
		public PatientBuilder setFiles(List<FileEntry> files) {
			((Patient) user).setFiles(files);
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
