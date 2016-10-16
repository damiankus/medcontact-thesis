package com.medcontact.data.validation;

public interface DataValidator<T> {
	boolean validate(T item);
}
