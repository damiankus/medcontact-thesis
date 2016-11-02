package com.medcontact.data.model;

import java.sql.SQLException;
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
import javax.sql.rowset.serial.SerialException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

@Entity
@Table(name="files")
@Data
@ToString(exclude="fileOwner")

public class FileEntry {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable=false)
	@NonNull
	private String name;
	
	@Column(nullable=false)
	@NonNull
	private Timestamp uploadTime;
	
	@Column(nullable=false)
	@NonNull
	private String contentType;
	
	@Column(nullable=false)
	@NonNull
	private String url;
	
	@Column(nullable=false)
	@JsonIgnore
	private String path;
	
	@Column(nullable=false)
	@JsonIgnore
	private long contentLength;
	
	@ManyToOne
	@JoinColumn(name="file_owner_id")
	@JsonIgnore
	private Patient fileOwner;
	
	public FileEntry() throws SerialException, SQLException {
		this.name = "";
		this.uploadTime = Timestamp.valueOf(LocalDateTime.now());
		this.url = "";
		this.path = "";
		this.contentType = "text/plain";
		this.contentLength = 0L;
		this.fileOwner = new Patient();
	}
}
