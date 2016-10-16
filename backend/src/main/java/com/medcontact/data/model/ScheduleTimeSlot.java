package com.medcontact.data.model;

import java.sql.Time;
import java.time.DayOfWeek;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name="schedules")
@Data
@AllArgsConstructor
public class ScheduleTimeSlot {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Enumerated(EnumType.STRING)
	private DayOfWeek dayOfWeek;
	private Time startTime;
	private Time endTime;
	
	@ManyToOne
	@JoinColumn(name="doctor_id")
	private Doctor doctor;

	public ScheduleTimeSlot() {
		super();
		this.dayOfWeek = DayOfWeek.MONDAY;
		this.startTime = Time.valueOf("00:00:00");
		this.endTime = Time.valueOf("01:00:00");
	}
	
	public ScheduleTimeSlot(DayOfWeek dayOfWeek, Time startTime, Time endTime) {
		super();
		this.dayOfWeek = dayOfWeek;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public ScheduleTimeSlot(DayOfWeek dayOfWeek, String startTime, String endTime) {
		super();
		this.dayOfWeek = dayOfWeek;
		this.startTime = Time.valueOf(startTime);
		this.endTime = Time.valueOf(endTime);
	}
}
