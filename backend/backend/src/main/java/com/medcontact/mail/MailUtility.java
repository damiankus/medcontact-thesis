package com.medcontact.mail;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class MailUtility {

	@Value("${spring.mail.host}")
	private String mailHost;
	
	@Value("${spring.mail.port}")
	private String mailPort;
	
	@Value("${spring.mail.username}")
	private String mailUsername;
	
	@Value("${spring.mail.password}")
	private String mailPassword;
	
	private Session session;
	private Address senderAddress;
	private Logger logger = Logger.getLogger(MailUtility.class.getName());
	
	@PostConstruct
	public void init() {
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.user", mailUsername);
		properties.setProperty("mail.smtp.host", mailHost);
		properties.setProperty("mail.smtp.port", mailPort);
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.socketFactory.fallback", "false");
		
		this.session = Session.getDefaultInstance(properties);
		
		try {
			this.senderAddress = new InternetAddress(mailUsername);
			
		} catch (AddressException e) {
			logger.severe(e.getMessage());
		}
	}
	
	public void sendMessage(Message message, String recipent) throws MessagingException {
		message.setFrom(this.senderAddress);
		message.setReplyTo(new Address[] { this.senderAddress });
		message.setSentDate(new Date());
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipent));
		
		Transport.send(message, mailUsername, mailPassword);
	}
	
}
