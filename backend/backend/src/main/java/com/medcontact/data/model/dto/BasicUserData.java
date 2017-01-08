package com.medcontact.data.model.dto;

import com.medcontact.data.model.domain.BasicUser;
import com.medcontact.data.model.enums.ApplicationRole;
import lombok.Data;

@Data
public class BasicUserData {
	
	protected long id;
	protected String email;
	protected String username;
	protected String firstName;
	protected String lastName;
	protected ApplicationRole role;
	
    protected String oldPassword;
    protected String newPassword1;
    protected String newPassword2;
	
	public BasicUserData() {
		this.id = 0L;
		this.email = "";
		this.username = "";
		this.firstName = "";
		this.lastName = "";
		this.role = ApplicationRole.PATIENT;
	}
	
	public void setEmail(String email) {
		this.email = email;
		this.username = email;
	}
	
	public void setUsername(String username) {
		this.email = username;
		this.username = username;
	}
	
	public BasicUserData(BasicUser user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.role = user.getRole();
	}
}
