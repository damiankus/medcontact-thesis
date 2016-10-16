package com.medcontact.data.model;

import java.sql.Blob;
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
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

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
	private Blob fileContent;
	
	@ManyToOne
	@JoinColumn(name="file_owner_id")
	private Patient fileOwner;
	
	public FileEntry() throws SerialException, SQLException {
		this.name = "";
		this.uploadTime = Timestamp.valueOf(LocalDateTime.now());
		this.fileContent = new SerialBlob(new byte[0]);
		this.fileOwner = new Patient();
	}
}
