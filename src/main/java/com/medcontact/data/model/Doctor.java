package com.medcontact.data.model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Check;

import lombok.Data;
import lombok.NonNull;

@Entity
@Table(name="doctors")
@DiscriminatorValue("DOCTOR")
@Check(constraints="rating > 0 AND rating <= 5")
@Data
public class Doctor extends BasicUser {
	private static final long serialVersionUID = -5663126536666561117L;
	
	@Column(nullable=false)
	@NonNull
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="doctor_specialty", 
			joinColumns={
					@JoinColumn(name="doctor_id", nullable=false)
			},
			inverseJoinColumns= {
					@JoinColumn(name="specialty_id", nullable=false)
			})
	private Set<Specialty> specialties;
	
	@Column(nullable=false)
	@NonNull
	private String university;
	private String biography;
	private float rating;
	
	@Column(nullable=false)
	@NonNull
	private String title;
	
	@OneToMany(mappedBy="ratedDoctor", fetch=FetchType.LAZY)
	private List<Opinion> opinions;
	
	public Doctor(String email, String password, String firstName, String lastName, Sex sex) {
		super(email, password, ApplicationRole.DOCTOR, firstName, lastName, sex);
	}
}
