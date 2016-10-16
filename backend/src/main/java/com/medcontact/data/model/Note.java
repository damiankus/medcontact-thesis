package com.medcontact.data.model;

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

import lombok.Data;

@Entity
@Table(name="notes")
@Data
public class Note {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable=false)
	private String title;
	
	@Column(nullable=false)
	private String content;
	
	@Column(nullable=false)
	private Timestamp uploadTime;
	
	@ManyToOne
	@JoinColumn(name="note_owner_id")
	private Doctor owner;
	
	@Column(name="patient_id", nullable=false)
	private long patientId;
	
	public Note() {
		this.title = "";
		this.content = "";
		this.uploadTime = Timestamp.valueOf(LocalDateTime.now());
		this.owner = new Doctor();
	}
}
