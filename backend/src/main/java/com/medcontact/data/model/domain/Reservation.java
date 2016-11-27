package com.medcontact.data.model.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import com.medcontact.data.model.enums.ReservationState;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

@Entity
@Table(name="reservations")
@Data
@ToString(exclude={"patient", "doctor"})
public class Reservation {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	@Column
	@Enumerated(EnumType.STRING)
	ReservationState reservationState;

	@Column(nullable=false)
	@NonNull
	private LocalDateTime startDateTime;
	
	@Column(nullable=false)
	@NonNull
	private LocalDateTime endDateTime;
	
	@ManyToOne
	@JoinColumn(name="patient_id")
	@JsonProperty(access=Access.WRITE_ONLY)
	private Patient patient;
	
	@OneToMany(mappedBy="reservation", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<SharedFile> sharedFiles;
	
	@ManyToOne
	@JoinColumn(name="doctor_id")
	private Doctor doctor;

	public Reservation(){
		this.reservationState = ReservationState.UNRESERVED;
	}
	
	public Reservation(Doctor doctor, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		this.doctor = doctor;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.reservationState = ReservationState.UNRESERVED;
	}
}
