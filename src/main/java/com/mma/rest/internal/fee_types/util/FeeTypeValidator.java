package com.mma.rest.internal.fee_types.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.mma.rest.internal.fee_types.resource.FeeTypeResource;
import com.mma.validation.ResourceValidator;

@Component
public class FeeTypeValidator extends ResourceValidator<FeeTypeResource> {

	private static final int MAX_LENGTH = 120;
	
	@Override
	protected void validateResource(FeeTypeResource resource, Errors errors) {
		
		// Validate name
		ValidationUtils.rejectIfEmpty(errors, "name", "required", "Please enter a name.");
		if(resource.getName().length() > MAX_LENGTH) {
			errors.rejectValue("name", "name", "Name length cannot be greater than " + MAX_LENGTH + ".");
		} 
		
		// Validate price
		ValidationUtils.rejectIfEmpty(errors, "price", "required", "Please enter a price.");
		if(resource.getPrice() <= 0) {
			errors.rejectValue("price", "price", "Price must be greater than 0.");
		}
	}

}
