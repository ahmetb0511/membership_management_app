package com.mma.rest.internal.users.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mma.common.converter.ResourceConverter;
import com.mma.common.enums.UserEnums;
import com.mma.common.enums.UserEnums.Role;
import com.mma.domain.User;
import com.mma.rest.internal.unit.resource.UnitConverter;
import com.mma.rest.internal.users.service.UserService;

@Component
public class UserConverter implements ResourceConverter<User, UserResource> {

	@Autowired
	private UnitConverter unitConverter;

	@Override
	public UserResource convert(User user) {
		UserResource resource = new UserResource();
		resource.setId(user.getId());
		resource.setEmail(user.getEmail());
		resource.setName(user.getName());
		
		if (user.getUnit() != null) {
			resource.setUnit(unitConverter.convert(user.getUnit()));
		}
		
		if(user.getRole() != null && user.getRole().getName().equalsIgnoreCase(UserService.ROLE_ADMIN)) {
			resource.setRole(Role.Administrator);
		} else if (user.getRole() != null && user.getRole().getName().equalsIgnoreCase(UserService.ROLE_MODERATOR)) {
			resource.setRole(Role.Moderator);
		} else {
			resource.setRole(Role.User);
		}
		
		resource.setEnabled(user.getStatus() == UserEnums.Status.ENABLED);
		resource.setPassword(user.getPassword());
		resource.setPassword2(user.getPassword());
		
		return resource;
	}
	
}
