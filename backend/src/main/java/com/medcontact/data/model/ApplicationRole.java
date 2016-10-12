package com.medcontact.data.model;

public enum ApplicationRole {
	ADMIN(1, "ADMIN"),
	DOCTOR(2, "DOCTOR"),
	PATIENT(3, "PATIENT");
	
	/* The value field may be used as the ID in the
	 * ApplicationAuthority wrapper class. */
	
	private long value;
	private String name;
	
	private ApplicationRole(long value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public long getValue() {
		return this.value;
	}
	
	/* WARNING: 
	 * Spring Security requires that the name of a role
	 * starts with a ROLE_ prefix. Because of that we've
	 * overridden the toString method. */
	
	@Override
	public String toString() {
		return "ROLE_" + this.name;
	}
}
