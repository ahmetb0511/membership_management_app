package com.mma.rest.internal.users.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.mma.common.enums.UserEnums;
import com.mma.domain.User;
import com.mma.rest.internal.users.resource.UserResource;
import com.mma.rest.internal.users.service.UserService;
import com.mma.validation.ResourceValidator;
import com.mma.validation.ResourceValidatorUtils;

@Component
public class UserValidator extends ResourceValidator<UserResource> {

	private static final int MAX_LENGTH = 100;
	
	@Autowired 
	private UserService userService;
	
	@Override
	protected void validateResource(UserResource resource, Errors errors) {
		
		// Validate name
		ValidationUtils.rejectIfEmpty(errors, "name", "required", "Please enter a name.");
		if(resource.getName().length() > MAX_LENGTH) {
			errors.rejectValue("name", "name", "Name length cannot be greater than " + MAX_LENGTH + ".");
		} 
		
		// New user is created
		if (resource.getId() == 0) {
			if (!resource.getEmail().isEmpty()) {
				if (!resource.getEmail().matches(ResourceValidatorUtils.EMAIL_REGEX)) {
					errors.rejectValue("email", "notvalid", "Email is not valid.");
				} else {
					User userByEmail = userService.getUserByEmailAndStatusNotDeleted(resource.getEmail());
					if(userByEmail != null && resource.getId() != userByEmail.getId()) {
						errors.rejectValue("email", "exist", "Email already exists.");
					}
				}
			}

			ValidationUtils.rejectIfEmpty(errors, "email", "required", "Please enter an email.");		
			
			if(resource.getPassword() != null && resource.getPassword2() != null) {
				ResourceValidatorUtils.checkPassword(resource.getPassword(), resource.getPassword2(), errors);	
			}
		}
		
		ValidationUtils.rejectIfEmpty(errors, "role", "required", "Please enter role.");
	}

	public void validateResetPassword(UserResource resource, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "emailToken", "emailToken", "Non valid code. Please enter link from e-mail again.");
		User user = userService.getUserByEmailToken(resource.getEmailToken());
		if(user == null) {
			errors.rejectValue("id", "user", "User with entered code not found.");
		} else if(user.getStatus() != UserEnums.Status.ENABLED) {
			errors.rejectValue("name", "name", "User with entered code is not active.");			
		} else {
			ResourceValidatorUtils.checkPassword(resource.getPassword(), resource.getPassword2(), errors);	
		}
	}

}
