package com.medcontact.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.medcontact.data.model.BasicUser;
import com.medcontact.data.model.BasicUserDetails;
import com.medcontact.data.model.Patient;

@Controller
public class LoggedInUserController {
	
	@RequestMapping(path="/who", produces="application/json")
	@ResponseBody
	public BasicUserDetails getCurrentUser() {
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		
		if (!(principal instanceof BasicUser) 
				|| principal == null) {
			principal = new Patient();
		}
		
		return new BasicUserDetails((BasicUser) principal);
	}
}
