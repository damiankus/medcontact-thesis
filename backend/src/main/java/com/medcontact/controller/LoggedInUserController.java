package com.medcontact.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.medcontact.data.model.domain.BasicUser;
import com.medcontact.data.model.dto.BasicUserDetails;
import com.medcontact.data.model.domain.Patient;

@Controller
public class LoggedInUserController {
	
	@RequestMapping(path="/who", produces="application/json")
	@ResponseBody
	public ResponseEntity<Object> getCurrentUser() {
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		
		Object body = null;
		HttpStatus status = HttpStatus.OK;
		
		if (principal == null
			|| !(principal instanceof BasicUser)) {
			
			Patient empty = new Patient();
			empty.setId(0L);
			body = empty;
			status = HttpStatus.NOT_FOUND;
			
		} else {
			body = new BasicUserDetails((BasicUser) principal);
			status = HttpStatus.OK;
		}
		
		return new ResponseEntity<Object>(body, status);
	}
}
