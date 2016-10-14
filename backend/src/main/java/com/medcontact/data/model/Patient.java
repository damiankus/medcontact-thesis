package com.medcontact.data.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="patients")
@DiscriminatorValue("PATIENT")
@Data
public class Patient extends BasicUser {
	private static final long serialVersionUID = -6160436846217117334L;
	
	@OneToMany(mappedBy="fileOwner", fetch=FetchType.LAZY)
	private List<FileEntry> files;
	
	@OneToMany(mappedBy="patient", fetch=FetchType.LAZY)
	private List<Reservation> reservations;
	
	@OneToMany(mappedBy="ratingPatient")
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
