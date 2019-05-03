package com.mma.rest.internal.users.resource;

import org.springframework.stereotype.Component;

import com.mma.common.converter.ResourceConverter;
import com.mma.domain.UserRight;

@Component
public class UserRightConverter implements ResourceConverter<UserRight, UserRightResource> {

	@Override
	public UserRightResource convert(UserRight userRight) {
		UserRightResource resource = new UserRightResource();
		resource.setCategory(userRight.getCategory());
		resource.setView(userRight.isView());
		resource.setEdit(userRight.isEdit());

		return resource;
	}

}
