package com.medcontact.data.model.builders;

import com.medcontact.data.model.domain.BasicUser;

public class BasicUserBuilder extends AbstractUserBuilder<BasicUser> {
	public BasicUserBuilder() {
		this.user = new BasicUser();
	}
}
