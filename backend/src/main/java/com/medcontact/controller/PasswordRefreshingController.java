package com.medcontact.controller;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.medcontact.data.model.domain.BasicUser;
import com.medcontact.data.model.dto.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.medcontact.data.repository.BasicUserRepository;
import com.medcontact.mail.MailUtility;


@Controller
@RequestMapping("passwords")
public class PasswordRefreshingController {
	private MailUtility mailUtility = new MailUtility();

	@Value("${general.host}")
	private String hostname;

	private Cache<String, String> refreshTokens = CacheBuilder.newBuilder()
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.concurrencyLevel(4)
			.build();

	@Autowired
	private BasicUserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping(value = "send-link")
	public ResponseEntity<String> sendRefreshMessage(
			@RequestParam("email") String email) throws MessagingException {

		String body;
		HttpStatus status;

		if (!userRepository.findByEmail(email).isPresent()) {
			body = "{\"status\": \"Email not found\"}";
			status = HttpStatus.BAD_REQUEST;

		} else {
			String refreshToken = "" + UUID.randomUUID() + "-"  + UUID.randomUUID();
			refreshTokens.put(email, refreshToken);

			MimeMessage message = new MimeMessage(mailUtility.getSession());
			message.setSubject("Zmiana hasła");
			message.setContent("Link do zmiany hasła: <a href=\""
					+ hostname + "passwords/" + refreshToken + "/set\">Zmiana hasła</a>",
					"text/html; charset=utf-8");
			mailUtility.sendMessage(message, email);

			status = HttpStatus.OK;
			body = "{\"status\": \"Message sent\"}";
		}

		return new ResponseEntity<String>(body, status);
	}

	@PostMapping(value = "{token}/set")
	public ResponseEntity<String> setPassword(
			@PathVariable("token") String token,
			@RequestBody Password password) throws MessagingException {

		String body;
		HttpStatus status;

		if(password.getPassword1().equals(password.getPassword2())) {
			String email = refreshTokens.getIfPresent(token);
			Optional<BasicUser> optionalUser = userRepository.findByEmail(email);
			if(optionalUser.isPresent()) {
				optionalUser.get().setPassword(passwordEncoder.encode(password.getPassword1()));
				body = "{\"status\": \"Password changed\"}";
				status = HttpStatus.OK;
			} else {
				body = "{\"status\": \"Invalid token\"}";
				status = HttpStatus.BAD_REQUEST;
			}
		} else {
			body = "{\"status\": \"Passwords not match\"}";
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<String>(body, status);
	}

}
