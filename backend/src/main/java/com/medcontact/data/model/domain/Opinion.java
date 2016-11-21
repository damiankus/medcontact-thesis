package com.medcontact.data.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Check;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name="opinions")
@Check(constraints="rating > 0 AND rating <= 5")
@ToString(exclude={"ratedDoctor", "ratingPatient"})
@Data
public class Opinion {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(length=2048)
	private String content;
	private int rating;
	
	@ManyToOne
	@JoinColumn(name="rated_doctor_id")
	private Doctor ratedDoctor;
	
	@ManyToOne
	@JoinColumn(name="rating_patient_id")
	private Patient ratingPatient;
}
