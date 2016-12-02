package com.medcontact.data.model.dto;

import com.medcontact.data.model.domain.BasicUser;
import com.medcontact.data.model.enums.ApplicationRole;
import lombok.Data;

@Data
public class BasicUserData {
	
	protected long id;
	protected String email;
	protected String firstName;
	protected String lastName;
	protected ApplicationRole role;
	
	public BasicUserData() {
		this.id = 0L;
		this.email = "";
		this.firstName = "";
		this.lastName = "";
		this.role = ApplicationRole.PATIENT;
	}
	
	public BasicUserData(BasicUser user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.role = user.getRole();
	}
}
