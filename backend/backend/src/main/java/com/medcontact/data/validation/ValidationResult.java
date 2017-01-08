package com.medcontact.data.validation;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ValidationResult {
	private List<String> errors;
	private boolean isValid;
	
	public ValidationResult() {
		this.errors = new ArrayList<>();
		this.isValid = false;
	}
	
	public void addError(String error) {
		this.errors.add(error);
	}
	
	public void addManyErrors(List<String> errors) {
		this.errors.addAll(errors);
	}
}
