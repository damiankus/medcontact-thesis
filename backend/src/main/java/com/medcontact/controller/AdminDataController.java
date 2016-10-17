package com.medcontact.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medcontact.data.repository.BasicUserRepository;

import lombok.Data;

@RestController
@RequestMapping("admins")
@Data
public class AdminDataController {
	private Logger logger = Logger.getLogger(AdminDataController.class.getName());
	
	@Autowired
	private BasicUserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
}
