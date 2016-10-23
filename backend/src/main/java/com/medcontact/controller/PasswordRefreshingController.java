package com.medcontact.controller;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.medcontact.data.repository.BasicUserRepository;
import com.medcontact.mail.MailUtility;


@Controller
@RequestMapping("password")
public class PasswordRefreshingController {
	private MailUtility mailHelper = new MailUtility();
	
	@Value("${general.host}")
	private String hostname;
	
	private Cache<String, String> refreshTokens = CacheBuilder.newBuilder()
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.concurrencyLevel(4)
			.build();
	
	@Autowired
	private BasicUserRepository userRepository;
	
	@PostMapping("send-link")
	public ResponseEntity<String> sendRefreshMessage(
			@RequestParam("email") String email) throws MessagingException {
		
		String body;
		HttpStatus status;
		
		if (!userRepository.findByEmail(email).isPresent()) {
			body = "Email not found";
			status = HttpStatus.BAD_REQUEST;
			
		} else {
			String refreshToken = "" + UUID.randomUUID() + "-"  + UUID.randomUUID();
			refreshTokens.put(email, refreshToken);
			
			MimeMessage message = new MimeMessage(mailHelper.getSession());
			message.setSubject("Zmiana hasła");
			message.setContent("Link do zmiany hasła: <a href=\"" 
					+ hostname + "password/refresh/" + refreshToken + "\">Zmiana hasła</a>",
					"text/html; charset=utf-8");
			mailHelper.sendMessage(message, email);
			
			status = HttpStatus.OK;
			body = "Message sent";
		}
		
		return new ResponseEntity<String>(body, status);
	}
	
}
