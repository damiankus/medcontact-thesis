package com.medcontact.data.model;

import java.util.ArrayList;
import java.util.Arrays;
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

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Data;
import lombok.NonNull;

@Entity
@Table(name="doctors")
@DiscriminatorValue("DOCTOR")
@Data
public class Doctor extends BasicUser {
	private static final long serialVersionUID = -5663126536666561117L;
	
	@Column(nullable=false)
	@NonNull
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
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
	private String roomId;
	
	@OneToMany(mappedBy="ratedDoctor", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<Opinion> opinions;
	
	@OneToMany(mappedBy="doctor", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<Reservation> reservations;
	
	@OneToMany(mappedBy="doctor", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<ScheduleTimeSlot> weeklySchedule;
	
	@OneToMany(mappedBy="owner", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<Note> notes;
	
	/* Setting default values. */
	
	public Doctor() {
		super();
		this.specialties = new ArrayList<>();
		this.title = "default";
		this.university = "default";
		this.biography = "default";
		this.opinions = new ArrayList<>();
		this.reservations = new ArrayList<>();
		this.weeklySchedule = new ArrayList<>();
		this.roomId = UUID.randomUUID().toString();
		this.notes = new ArrayList<>();
		
		this.authorities = Arrays.asList(
				new SimpleGrantedAuthority(
						ApplicationRole.DOCTOR.toString()));
	}
	
	public static DoctorBuilder getBuilder() {
		return new DoctorBuilder();
	}
	
	public static class DoctorBuilder extends BasicUser.BasicUserBuilder {
		public DoctorBuilder() {
			this.user = new Doctor();
		}
		
		public DoctorBuilder setTitle(String title) {
			((Doctor) user).setTitle(title);
			return this;
		}
		
		public DoctorBuilder setUniversity(String university) {
			((Doctor) user).setUniversity(university);
			return this;
		}
		
		public DoctorBuilder setBiography(String biography) {
			((Doctor) user).setBiography(biography);
			return this;
		}
		
		public DoctorBuilder setSpecialties(List<Specialty> specialties) {
			((Doctor) user).setSpecialties(specialties);
			return this;
		}
		
		public DoctorBuilder setOpinions(List<Opinion> opinions) {
			((Doctor) user).setOpinions(opinions);
			return this;
		}
		
		public DoctorBuilder setReservations(List<Reservation> reservations) {
			((Doctor) user).setReservations(reservations);
			return this;
		}
		
		public DoctorBuilder setWeeklySchedule(List<ScheduleTimeSlot> schedule) {
			((Doctor) user).setWeeklySchedule(schedule);
			return this;
		}
		
		public DoctorBuilder setRoomId(String roomId) {
			((Doctor) user).setRoomId(roomId);
			return this;
		}
		
		public DoctorBuilder setNotes(List<Note> notes) {
			((Doctor) user).setNotes(notes);
			return this;
		}
	}
}
