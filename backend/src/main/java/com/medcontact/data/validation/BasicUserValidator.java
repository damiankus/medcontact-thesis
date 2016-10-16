package com.medcontact.data.validation;

import java.util.regex.Pattern;

import com.medcontact.data.model.BasicUser;

public class BasicUserValidator extends DataValidatorHelper<BasicUser> {
	public final int MAX_EMAIL_LEN = 128;
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
	public boolean validate(BasicUser user) {
		return isEmailValid(user.getEmail())
				&& isStringLengthValid(user.getPassword(), MAX_PASSWORD_LEN)
				&& isStringLengthValid(user.getFirstName(), MAX_FIRST_NAME_LEN)
				&& isStringLengthValid(user.getLastName(), MAX_LAST_NAME_LEN);
	}
}
