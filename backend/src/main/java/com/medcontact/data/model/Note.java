package com.medcontact.data.model;

import java.sql.Blob;
import java.sql.Timestamp;

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
	private Timestamp uploadTime;
	
	@Column(nullable=false)
	private Blob fileContent;
	
	@ManyToOne
	@JoinColumn(name="note_owner_id")
	private Doctor owner;
	
	@Column(name="patient_id", nullable=false)
	private long patientId;
	
}
