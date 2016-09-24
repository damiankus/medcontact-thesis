package com.medcontact.data.model;

import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.http.auth.BasicUserPrincipal;
import org.hibernate.annotations.Check;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.medcontact.data.model.BasicUser.AbstractUserBuilder;

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
	private List<Specialty> specialties;
	
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
	
	/* Setting default values. */
	
	public Doctor() {
		super();
		this.specialties = new ArrayList<>();
		this.title = "default";
		this.university = "default";
		this.biography = "default";
		this.rating = 0;
		this.opinions = new ArrayList<>();
		
		this.authorities = Arrays.asList(
				new SimpleGrantedAuthority(
						ApplicationRole.DOCTOR.toString()));
	}
	
	public Doctor(String email, String password, String firstName, String lastName, Sex sex) {
		super(email, password, ApplicationRole.DOCTOR, firstName, lastName, sex);
	}
	
	public static DoctorBuilder getBuilder() {
		return new DoctorBuilder();
	}
	
	public static class DoctorBuilder extends BasicUser.BasicUserBuilder {
		public DoctorBuilder() {
			this.user = new Doctor();
		}
		
		public DoctorBuilder setTitle(String title) {
			((Doctor) user).setTitle(title);
			return this;
		}
		
		public DoctorBuilder setUniversity(String university) {
			((Doctor) user).setUniversity(university);
			return this;
		}
		
		public DoctorBuilder setBiography(String biography) {
			((Doctor) user).setBiography(biography);
			return this;
		}
		
		public DoctorBuilder setRating(float rating) {
			((Doctor) user).setRating(rating);
			return this;
		}
		
		public DoctorBuilder setSpecialties(List<Specialty> specialties) {
			((Doctor) user).setSpecialties(specialties);
			return this;
		}
		
		public DoctorBuilder setOpinions(List<Opinion> opinions) {
			((Doctor) user).setOpinions(opinions);
			return this;
		}
	}
}
