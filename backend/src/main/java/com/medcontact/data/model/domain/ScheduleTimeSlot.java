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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

@Entity
@Table(name="schedules")
@Data
@AllArgsConstructor
@ToString(exclude="doctor")
public class ScheduleTimeSlot {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable=false)
	@NonNull
	private LocalDateTime startDateTime;
	
	@Column(nullable=false)
	@NonNull
	private LocalDateTime endDateTime;
	
	@ManyToOne
	@JoinColumn(name="doctor_id")
	private Doctor doctor;

	public ScheduleTimeSlot() {
		super();
		this.startDateTime = LocalDateTime.of(2000, 1, 1, 1, 0);
		this.endDateTime = LocalDateTime.of(2100, 12, 31, 23, 59);
	}
	
	public ScheduleTimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
		super();
		this.startDateTime = startTime;
		this.endDateTime = endTime;
	}
	
	public ScheduleTimeSlot(String startTime, String endTime) {
		super();
		this.startDateTime = LocalDateTime.parse(startTime);
		this.endDateTime = LocalDateTime.parse(endTime);
	}
}
