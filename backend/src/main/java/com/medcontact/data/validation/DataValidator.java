package com.medcontact.data.validation;

public interface DataValidator<T> {
	public ValidationResult validate(T item);
}
