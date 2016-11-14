package com.medcontact.security.config;

import com.medcontact.data.model.domain.BasicUser;
import com.medcontact.exception.UnauthorizedUserException;

public interface EntitlementValidator {
	
	public boolean isEntitled(Long userId, Class<? extends BasicUser> userType) throws UnauthorizedUserException;
}
