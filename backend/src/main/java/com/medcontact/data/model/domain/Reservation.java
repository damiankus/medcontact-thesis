package com.medcontact.data.model.domain;

import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.domain.Patient;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

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
	private Date date;
	
	@Column(nullable=false)
	@NonNull
	private Time startTime;
	
	@Column(nullable=false)
	@NonNull
	private Time endTime;
	
	@ManyToOne
	@JoinColumn(name="patient_id")
	@JsonProperty(access=Access.WRITE_ONLY)
	private Patient patient;
	
	@ManyToOne
	@JoinColumn(name="doctor_id")
	private Doctor doctor;
	
	public Reservation() {
		this.date = Date.valueOf(LocalDate.now());
		this.startTime = Time.valueOf("00:00:00");
		this.startTime = Time.valueOf("00:00:01");
	}
	
}
