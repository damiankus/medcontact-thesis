package com.medcontact.security.config;

import java.util.logging.Logger;

import org.springframework.security.core.context.SecurityContextHolder;

import com.medcontact.data.model.domain.BasicUser;
import com.medcontact.exception.UnauthorizedUserException;

public class BasicUserEntitlementValidator implements EntitlementValidator {
	private Logger logger = Logger.getLogger(BasicUserEntitlementValidator.class.getName());
	
	@Override
	public boolean isEntitled(Long userId, Class<? extends BasicUser> userType)
			throws UnauthorizedUserException {
		
		Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
		
        if (!(userType.isInstance(principal))
                || (!((BasicUser) principal).getId().equals(userId))) {

            logger.warning("An attempt to upload a file without authorization detected - " + principal.toString());
            throw new UnauthorizedUserException();
        }

        return true;
	}

}
