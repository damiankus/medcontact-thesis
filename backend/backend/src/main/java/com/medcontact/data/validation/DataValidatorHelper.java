package com.medcontact.data.validation;

import java.util.Iterator;
import java.util.List;

public class DataValidatorHelper<T> implements DataValidator<T> {
	protected boolean isStringLengthValid(String value, int minLength, int maxLength) {
		if (minLength > maxLength) {
			int tmp = minLength;
			minLength = maxLength;
			maxLength = tmp;
		}
		
		return (value != null
				&& value.length() >= minLength
				&& value.length() <= maxLength);
	}
	
	protected boolean isStringLengthValid(String value, int maxLength) {
		return (value != null
				&& value.length() > 0
				&& value.length() <= maxLength);
	}
	
	protected boolean isIntegerValid(int value, int minValue, int maxValue) {
		return (value >= minValue && value <= maxValue);
	}
	
	protected <U> boolean isListSizeValid(List<U> list, int maxSize) {
		return (list != null
				&& list.size() <= maxSize);
	}
	
	protected <U> ValidationResult isListValid(
			List<U> list, String listName, int maxSize, DataValidator<U> validator) {
		
		ValidationResult result = new ValidationResult();
		boolean isValid = (list != null && list.size() <= maxSize);
		Iterator<U> it = list.iterator();
		int count = 1;
		
		while (isValid && it.hasNext()) {
			ValidationResult itemValidationResult = validator.validate(it.next());
			isValid = isValid && itemValidationResult.isValid();
			
			if (!isValid) {
				for (String e : itemValidationResult.getErrors()) {
					result.addError("[" + listName + ", item [" + count + "]]: " + e);
				}
			}
				
			count++;
		}
		
		result.setValid(isValid);
		return result;
	}

	@Override
	public ValidationResult validate(T item) {
		return new ValidationResult();
	}
}
