package com.medcontact.data.model;

import lombok.Data;

@Data
public class BasicUserDetails {
	
	protected long id;
	protected String email;
	protected String firstName;
	protected String lastName;
	protected ApplicationRole role;
	
	public BasicUserDetails() {
		this.id = 0L;
		this.email = "";
		this.firstName = "";
		this.lastName = "";
		this.role = ApplicationRole.PATIENT;
	}
	
	public BasicUserDetails(BasicUser user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.role = user.getRole();
	}
}
