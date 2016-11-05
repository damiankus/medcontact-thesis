package com.medcontact.data.validation;

import com.medcontact.data.model.domain.Opinion;

public class OpinionValidator extends DataValidatorHelper<Opinion> {
	public final int MAX_OPINION_LEN = 2048;
	public final int MAX_OPINION_RATING = 5;
	
	@Override
	public ValidationResult validate(Opinion opinion) {
		ValidationResult result = new ValidationResult();
		
		if (!isStringLengthValid(opinion.getContent(), MAX_OPINION_LEN)) {
			result.addError("Invalid opinion content length");
			
		} else if (!isIntegerValid(opinion.getRating(), 0, MAX_OPINION_RATING)) {
			result.addError("Invalid rating value");
			
		} else {
			result.setValid(true);
		}
		
		return result;
	}
}
