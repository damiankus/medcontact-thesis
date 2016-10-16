package com.medcontact.data.validation;

import java.util.regex.Pattern;

import com.medcontact.data.model.BasicUser;

public class BasicUserValidator extends DataValidatorHelper<BasicUser> {
	public final int MAX_EMAIL_LEN = 128;
	public final int MIN_PASSWORD_LEN = 6;
	public final int MAX_PASSWORD_LEN = 128;
	public final int MAX_FIRST_NAME_LEN = 128;
	public final int MAX_LAST_NAME_LEN = 128;
	public final String EMAIL_PATTERN = ".+@.+.+";
	
	private boolean isEmailValid(String email) {
		return (email.length() <= MAX_EMAIL_LEN)
				&& Pattern.compile(EMAIL_PATTERN)
					.matcher(email)
					.find();
	}
	
	@Override
	public ValidationResult validate(BasicUser user) {
		ValidationResult result = new ValidationResult();
		
		if (!isEmailValid(user.getEmail())) {
			result.getErrors().add("Adres e-mail too long");
		} else if (!isStringLengthValid(user.getPassword(), MIN_PASSWORD_LEN, MAX_PASSWORD_LEN)) {
			result.addError("Invalid password length");
		} else if (!isStringLengthValid(user.getFirstName(), MAX_FIRST_NAME_LEN)) {
			result.addError("Invalid first name length");
		} else if (!isStringLengthValid(user.getLastName(), MAX_LAST_NAME_LEN)) {
			result.addError("Invalid last name length");
		} else {
			result.setValid(true);
		}
		
		return result;
	}
}
