package com.mma.rest.internal.fees.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mma.common.converter.ResourceConverter;
import com.mma.domain.Fee;
import com.mma.rest.internal.fee_types.resource.FeeTypeConverter;
import com.mma.rest.internal.unit.resource.UnitConverter;
import com.mma.rest.internal.users.resource.UserConverter;

@Component
public class FeeConverter implements ResourceConverter<Fee, FeeResource>{

	@Autowired
	private UnitConverter unitConverter;
	
	@Autowired
	private UserConverter userConverter;
	
	@Autowired 
	private FeeTypeConverter feeTypeConverter;

	@Override
	public FeeResource convert(Fee fee) {
		FeeResource resource = new FeeResource();
		resource.setId(fee.getId());
		
		if (fee.getUnit() != null) {
			resource.setUnit(unitConverter.convert(fee.getUnit()));
		}
		
		if (fee.getUser() != null) {
			resource.setUser(userConverter.convert(fee.getUser()));
		}
		
		if (fee.getFeeType() != null) {
			resource.setFeeType(feeTypeConverter.convert(fee.getFeeType()));
		}
		
		resource.setTimeAdded(fee.getTimeAdded());
		
		return resource;
	}

}
