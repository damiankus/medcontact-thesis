package com.medcontact.data.validation;

import com.medcontact.data.model.Specialty;

public class SpecialtyValidator extends DataValidatorHelper<Specialty> {
	public final int MAX_SPECIALTY_LEN = 128;
	public final int MAX_CATEGORY_LEN = 128;
	
	@Override
	public boolean validate(Specialty specialty) {
		return (specialty.getName().length() <= MAX_SPECIALTY_LEN)
				&& (specialty.getCategory().length() <= MAX_CATEGORY_LEN);
	}
}
