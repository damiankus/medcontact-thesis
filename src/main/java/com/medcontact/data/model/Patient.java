package com.medcontact.data.model;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="patients")
@DiscriminatorValue("PATIENT")
@Data
public class Patient extends BasicUser {
	private static final long serialVersionUID = -6160436846217117334L;
	
	@OneToMany(mappedBy="fileOwner", fetch=FetchType.LAZY)
	private List<FileEntry> files;
	
	public Patient(String email, String password, String firstName, String lastName, Sex sex) {
		super(email, password, ApplicationRole.PATIENT, firstName, lastName, sex);
	}	
}
