package com.medcontact.data.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="specialties")
@Data
public class Specialty {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String name;
	private String category;
	
	@ManyToMany(fetch=FetchType.LAZY, mappedBy="specialties")
	private Set<Doctor> doctorsWithSpecialty;
}