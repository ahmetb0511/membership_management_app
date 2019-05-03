package com.mma.validation;

import java.util.Set;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CompositeResourceValidator implements Validator {

	private final Set<ResourceValidator<?>> validators;
	
	public CompositeResourceValidator(Set<ResourceValidator<?>> validators) {
		this.validators = validators;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		for (ResourceValidator<?> validator : validators) {
			if (validator.supports(clazz)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void validate(Object target, Errors errors) {
		for (ResourceValidator<?> validator : validators) {
			if (validator.supports(target.getClass())) {
				validator.validate(target, errors);
			}
		}
	}
	
}
