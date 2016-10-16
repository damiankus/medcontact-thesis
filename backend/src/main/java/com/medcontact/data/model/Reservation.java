package com.medcontact.data.model;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

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
	
	public Reservation() {
		this.date = Date.valueOf(LocalDate.now());
		this.startTime = Time.valueOf("00:00:00");
		this.startTime = Time.valueOf("01:00:00");
	}
	
}
