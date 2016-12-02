package com.medcontact.data.model.builders;

import com.medcontact.data.model.domain.Admin;

public class AdminBuilder extends BasicUserBuilder {
	public AdminBuilder() {
		this.user = new Admin();
	}
}
