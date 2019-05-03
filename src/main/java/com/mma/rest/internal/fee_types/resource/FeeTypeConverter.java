package com.mma.rest.internal.fee_types.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mma.common.converter.ResourceConverter;
import com.mma.domain.FeeType;
import com.mma.rest.internal.unit.resource.UnitConverter;

@Component
public class FeeTypeConverter implements ResourceConverter<FeeType, FeeTypeResource>{

	@Autowired
	private UnitConverter unitConverter;

	@Override
	public FeeTypeResource convert(FeeType feeType) {
		FeeTypeResource resource = new FeeTypeResource();
		resource.setId(feeType.getId());
		resource.setName(feeType.getName());
		resource.setDescription(feeType.getDescription());
		resource.setPrice(feeType.getPrice());
		
		if (feeType.getUnit() != null) {
			resource.setUnit(unitConverter.convert(feeType.getUnit()));
		}
		
		return resource;
	}
}
