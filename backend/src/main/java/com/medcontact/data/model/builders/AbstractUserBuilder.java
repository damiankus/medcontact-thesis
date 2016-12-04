package com.medcontact.data.model.builders;

import com.medcontact.data.model.domain.BasicUser;

/* 
 * For the sake of convenience we provide a fluent API
 * for building BasicUser objects.
 *  */

public abstract class AbstractUserBuilder<T extends BasicUser> {
	protected T user;
			
	public AbstractUserBuilder<T> setPassword(String password) {
		user.setPassword(password);
		return this;
	}
	
	public AbstractUserBuilder<T> setFirstName(String firstName) {
		user.setFirstName(firstName);
		return this;
	}
	
	public AbstractUserBuilder<T> setLastName(String lastName) {
		user.setLastName(lastName);
		return this;
	}
	
	public AbstractUserBuilder<T> setEmail(String email) {
		user.setEmail(email);
		return this;
	}
	
	public AbstractUserBuilder<T> valueOf(BasicUser otherUser) {
		user.setPassword(otherUser.getPassword());
		user.setEmail(otherUser.getEmail());
		user.setFirstName(otherUser.getFirstName());
		user.setLastName(otherUser.getLastName());
		
		return this;
	}
	
	public T build() {
		return this.user;
	}
}