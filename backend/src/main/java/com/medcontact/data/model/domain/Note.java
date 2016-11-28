package com.medcontact.data.model.domain;

import java.sql.Timestamp;
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
import lombok.ToString;

@Entity
@Table(name="notes")
@Data
@ToString(exclude="doctor")
public class Note {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable=false, length=1000)
	private String content;
	
	@Column(nullable=false)
	private Timestamp uploadTime;
	
	@ManyToOne
	@JoinColumn(name="doctor_id")
	@JsonProperty(access=Access.WRITE_ONLY)
	private Doctor doctor;
	
	@ManyToOne
	@JoinColumn(name="patient_id")
	@JsonProperty(access=Access.WRITE_ONLY)
	private Patient patient;
	
	public Note() {
		this.content = "";
		this.uploadTime = Timestamp.valueOf(LocalDateTime.now());
	}
}
