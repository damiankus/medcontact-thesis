package com.medcontact.data.model.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name="specialties")
@Data
@ToString(exclude={ "doctorsWithSpecialty" })
@EqualsAndHashCode(exclude={ "doctorsWithSpecialty" })
public class Specialty {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String name;
	
	@ManyToMany(fetch=FetchType.LAZY, mappedBy="specialties")
	@JsonIgnore
	private Set<Doctor> doctorsWithSpecialty;
	
	public Specialty() {
		this.name = "";
		this.doctorsWithSpecialty = new HashSet<>();
	}
	
	public Specialty(String name) {
		this.name = name;
		this.doctorsWithSpecialty = new HashSet<>();
	}
}
