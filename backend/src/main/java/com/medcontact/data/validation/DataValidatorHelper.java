package com.medcontact.data.validation;

import java.util.Iterator;
import java.util.List;

public class DataValidatorHelper<T> implements DataValidator<T> {
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
	
	protected <U> boolean isListValid(
			List<U> list, int maxSize, DataValidator<U> validator) {
		
		boolean isValid = (list != null && list.size() <= maxSize);
		Iterator<U> it = list.iterator();
		
		while (isValid && it.hasNext()) {
			isValid = isValid && (validator.validate(it.next()));
		}
		
		return isValid;
	}

	@Override
	public boolean validate(T item) {
		return false;
	}
}
