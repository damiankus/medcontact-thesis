package com.medcontact.data.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="administrators")
@DiscriminatorValue("ADMIN")
@Data
public class Administrator extends BasicUser {
	private static final long serialVersionUID = 820358146018064860L;
	
	public Administrator(String email, String password, String firstName, String lastName, Sex sex) {
		super(email, password, ApplicationRole.ADMIN, firstName, lastName, sex);
	}

}
