package com.medcontact.data.validation;

import com.medcontact.data.model.Opinion;

public class OpinionValidator extends DataValidatorHelper<Opinion> {
	public final int MAX_OPINION_LEN = 2048;
	public final int MAX_OPINION_RATING = 5;
	
	@Override
	public boolean validate(Opinion opinion) {
		return (isStringLengthValid(opinion.getContent(), MAX_OPINION_LEN))
				&& (isIntegerValid(opinion.getRating(), 0, MAX_OPINION_RATING));
	}
}
