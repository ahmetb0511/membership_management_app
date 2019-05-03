package com.mma.rest.internal.unit.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.mma.domain.Unit;
import com.mma.rest.internal.unit.resource.UnitResource;
import com.mma.rest.internal.unit.service.UnitService;
import com.mma.validation.ResourceValidator;
import com.mma.validation.ResourceValidatorUtils;

@Component
public class UnitValidator extends ResourceValidator<UnitResource> {

	private static final int MAX_LENGTH = 90;
	
	@Autowired 
	private UnitService unitService;
	
	@Override
	protected void validateResource(UnitResource resource, Errors errors) {
		
		// Validate name
		ValidationUtils.rejectIfEmpty(errors, "name", "required", "Please enter a name.");
		if(resource.getName().length() > MAX_LENGTH) {
			errors.rejectValue("name", "name", "Name length cannot be greater than " + MAX_LENGTH + ".");
		} 
		
		// New unit is created
		if (resource.getId() == 0) {
			if (!resource.getSupportEmail().isEmpty()) {
				if (!resource.getSupportEmail().matches(ResourceValidatorUtils.EMAIL_REGEX)) {
					errors.rejectValue("supportEmail", "notvalid", "Support email is not valid.");
				} else {
					Unit unitByEmail = unitService.getUnitBySupportEmailAndStatusNotDeleted(resource.getSupportEmail());
					if(unitByEmail != null && resource.getId() != unitByEmail.getId()) {
						errors.rejectValue("email", "exist", "Support email already exists.");
					}
				}
			}

			ValidationUtils.rejectIfEmpty(errors, "supportEmail", "required", "Please enter an support email.");		
			
		}
	}

}
