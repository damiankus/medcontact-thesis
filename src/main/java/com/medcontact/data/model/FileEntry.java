package com.medcontact.data.model;

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
@Table(name="files")
@Data
public class FileEntry {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable=false)
	private String description;
	
	@Column(nullable=false)
	private LocalDateTime uploadTime;
	
	@Column(nullable=false)
	private byte[] fileContent;
	
	@ManyToOne
	@JoinColumn(name="file_owner_id")
	private Patient fileOwner;
}
