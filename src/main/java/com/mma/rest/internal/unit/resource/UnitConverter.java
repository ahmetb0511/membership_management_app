package com.mma.rest.internal.unit.resource;

import org.springframework.stereotype.Component;

import com.mma.common.converter.ResourceConverter;
import com.mma.common.enums.UnitEnums;
import com.mma.domain.Unit;

@Component
public class UnitConverter implements ResourceConverter<Unit, UnitResource> {

	@Override
	public UnitResource convert(Unit unit) {
		UnitResource resource = new UnitResource();
		resource.setId(unit.getId());
		resource.setName(unit.getName());
		resource.setSupportEmail(unit.getSupportEmail());
		resource.setEnabled(unit.getStatus() == UnitEnums.Status.ACTIVE);
		return resource;
	}

}
