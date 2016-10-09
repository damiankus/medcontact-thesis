package com.medcontact.data.model;

import lombok.Data;

@Data
public class BasicUserDetails {
	
	protected String password;
	protected String email;
	protected String firstName;
	protected String lastName;
	protected Sex sex;
	
	public BasicUserDetails() {
		this.password = "";
		this.email = "";
		this.firstName = "";
		this.lastName = "";
		this.sex = Sex.OTHER;
	}
}
