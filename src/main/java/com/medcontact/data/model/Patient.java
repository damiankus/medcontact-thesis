package com.medcontact.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Data;

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
		this.authorities = Arrays.asList(
				new SimpleGrantedAuthority(
						ApplicationRole.PATIENT.toString()));
	}
	
	public Patient(String email, String password, String firstName, String lastName, Sex sex) {
		super(email, password, ApplicationRole.PATIENT, firstName, lastName, sex);
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
