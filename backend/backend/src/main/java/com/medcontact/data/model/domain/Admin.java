package com.medcontact.data.model.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.medcontact.data.model.builders.AdminBuilder;
import com.medcontact.data.model.enums.ApplicationRole;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="admins")
@DiscriminatorValue("ADMIN")
@Data

/* This annotations prevents the lombok library
 * from calling the superclass' equals and hashCode 
 * methods thus preventing warnings about
 * overriding the mentioned methods. */

@EqualsAndHashCode(callSuper=false)

public class Admin extends BasicUser {
	private static final long serialVersionUID = 820358146018064860L;
	
	public Admin() {
		
		/* This call sets the default values of the
		 * user data. */
		
		super();
		this.role = ApplicationRole.ADMIN;
	}
	
	public static AdminBuilder getBuilder() {
		return new AdminBuilder();
	}

}
