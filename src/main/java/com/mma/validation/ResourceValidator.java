package com.mma.validation;

import org.springframework.core.ResolvableType;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public abstract class ResourceValidator<T> implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		ResolvableType type = ResolvableType.forType(this.getClass().getGenericSuperclass());
		return type.hasGenerics() && type.getGeneric(0).getRawClass().equals(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validate(Object target, Errors errors) {
		validateResource((T) target, errors);
	}
	
	protected abstract void validateResource(T resource, Errors errors);

}
