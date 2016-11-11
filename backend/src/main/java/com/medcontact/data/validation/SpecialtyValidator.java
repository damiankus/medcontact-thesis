package com.medcontact.data.validation;

import com.medcontact.data.model.domain.Specialty;

public class SpecialtyValidator extends DataValidatorHelper<Specialty> {
	public final int MAX_SPECIALTY_LEN = 128;
	public final int MAX_CATEGORY_LEN = 128;
	
	@Override
	public ValidationResult validate(Specialty specialty) {
		ValidationResult result = new ValidationResult();
		
		if (!isStringLengthValid(specialty.getName(), MAX_SPECIALTY_LEN)) {
			result.addError("Invalid specialty name length");
			
		} else if (!isStringLengthValid(specialty.getCategory(), MAX_CATEGORY_LEN)) {
			result.addError("Invalid category name length");
			
		} else {
			result.setValid(true);
		}
		
		return result;
	}
}
