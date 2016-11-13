package com.medcontact.data.model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.medcontact.data.model.enums.ApplicationRole;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Entity
@Table(name="doctors")
@DiscriminatorValue("DOCTOR")
@Data

/* This annotations prevents the lombok library
 * from calling the superclass' equals and hashCode 
 * methods thus preventing warnings about
 * overriding the mentioned methods. */

@EqualsAndHashCode(callSuper=false)
public class Doctor extends BasicUser {
	private static final long serialVersionUID = -5663126536666561117L;
	
	@Column(nullable=false)
	@NonNull
	@ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinTable(name="doctor_specialty", 
			joinColumns={
					@JoinColumn(name="doctor_id", nullable=false)
			},
			inverseJoinColumns= {
					@JoinColumn(name="specialty_id", nullable=false)
			})
	private List<Specialty> specialties;
	
	@Column(nullable=false)
	@NonNull
	private String university;
	private String biography;
	
	@Column(nullable=false)
	@NonNull
	private String title;
	
	@Column(nullable=false)
	@NonNull
	@JsonIgnore
	private String roomId;
	
	/* The "busy" field tells whether the doctor
	 * is currently seeing any patient. */
	
	@Column(nullable=false)
	private boolean available;
	
	@OneToMany(mappedBy="ratedDoctor", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JsonProperty(access=Access.WRITE_ONLY)
	private List<Opinion> opinions;
	
	@OneToMany(mappedBy="doctor", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JsonProperty(access=Access.WRITE_ONLY)
	private List<Reservation> reservations;
	
	@OneToMany(mappedBy="doctor", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JsonProperty(access=Access.WRITE_ONLY)
	private List<ScheduleTimeSlot> weeklySchedule;
	
	@OneToMany(mappedBy="doctor", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JsonProperty(access=Access.WRITE_ONLY)
	private List<Note> notes;
	
	@OneToMany(mappedBy="doctor", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<SharedFile> sharedFiles;
	
	/* Setting default values. */
	
	public Doctor() {
		super();
		this.specialties = new ArrayList<>();
		this.title = "";
		this.university = "";
		this.biography = "";
		this.available = false;
		
		this.opinions = new ArrayList<>();
		this.reservations = new ArrayList<>();
		this.weeklySchedule = new ArrayList<>();
		this.roomId = UUID.randomUUID().toString();
		this.notes = new ArrayList<>();
		this.role = ApplicationRole.DOCTOR;
	}

	public void addSchedule(ScheduleTimeSlot scheduleTimeSlot) {
		weeklySchedule.add(scheduleTimeSlot);
	}

	public void addReservation(Reservation reservation) {
		reservations.add(reservation);
	}
	
	public void toggleAvailability() {
		this.available = !this.available;
	}
}
