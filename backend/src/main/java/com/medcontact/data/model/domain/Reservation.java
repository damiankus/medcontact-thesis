package com.medcontact.data.model.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

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
	
	@ManyToOne
	@JoinColumn(name="doctor_id")
	private Doctor doctor;
	
	public Reservation() {
		this.startDateTime = LocalDateTime.of(2000, 1, 1, 1, 0);
		this.endDateTime = LocalDateTime.of(2100, 12, 31, 23, 59);
	}

	public Reservation(Patient patient, Doctor doctor, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		this.startDateTime = LocalDateTime.of(2000, 1, 1, 1, 0);
		this.endDateTime = LocalDateTime.of(2100, 12, 31, 23, 59);
	}
}
