package com.medcontact.data.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name="reservations")
@Data
public class Reservation {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private Date date;
	private Time startTime;
	private Time endTime;
	
	@ManyToOne
	@JoinColumn(name="patient_id") 
	private Patient patient;
	
	@ManyToOne
	@JoinColumn(name="doctor_id")
	private Doctor doctor;
	
}
