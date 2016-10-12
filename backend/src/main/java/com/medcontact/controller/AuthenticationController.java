package com.medcontact.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

	@GetMapping(value="login")
	public String getLoginForm() {
		return "login";
	}
	
	/* Invalidate the current session and redirect the 
	 * user to the home page. */
	
	@GetMapping(value="logout")
	public String logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
		return "home";
	}
}
