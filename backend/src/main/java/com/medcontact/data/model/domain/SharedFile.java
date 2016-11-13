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

import lombok.Data;
import lombok.NonNull;

@Entity
@Table(name="shared_files")
@Data

public class SharedFile {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Long id;
	
	@ManyToOne
	@JoinColumn(name="file_entry_id")
	private FileEntry fileEntry;
	
	@Column(nullable=false)
	@NonNull
	private LocalDateTime expirationTime;
	
	@Column(nullable=false)
	@NonNull
	@JoinColumn(name="doctor_id")
	private Doctor doctor;
	
}
