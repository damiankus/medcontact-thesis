package com.medcontact.data.model;

import java.util.Arrays;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Data;

@Entity
@Table(name="admins")
@DiscriminatorValue("ADMIN")
@Data
public class Admin extends BasicUser {
	private static final long serialVersionUID = 820358146018064860L;
	
	public Admin() {
		
		/* This call sets the default values of the
		 * user data. */
		super();
		this.authorities = Arrays.asList(
				new SimpleGrantedAuthority(
						ApplicationRole.ADMIN.toString()));
	}
	
	public Admin(String email, String password, String firstName, String lastName, Sex sex) {
		super(email, password, ApplicationRole.ADMIN, firstName, lastName, sex);
	}
	
	public static AdminBuilder getBuilder() {
		return new AdminBuilder();
	}
	
	public static class AdminBuilder extends BasicUser.BasicUserBuilder {
		public AdminBuilder() {
			this.user = new Admin();
		}
	}
}
