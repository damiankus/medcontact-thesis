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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;

@Entity
@Table(name="files")
@Data
public class FileEntry {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=false)
	private Timestamp uploadTime;
	
	@Column(nullable=false)
	private String contentType;
	
	@Column(nullable=false)
	private String url;
	
	@ManyToOne
	@JoinColumn(name="file_owner_id")
	@JsonProperty(access=Access.WRITE_ONLY)
	private Patient fileOwner;
	
	public FileEntry() throws SerialException, SQLException {
		this.name = "";
		this.uploadTime = Timestamp.valueOf(LocalDateTime.now());
		this.url = "";
		this.contentType = "text/plain";
		this.fileOwner = new Patient();
	}
}
