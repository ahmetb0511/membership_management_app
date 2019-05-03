package com.mma.rest.internal.fees.resource;

import java.util.Date;

import com.mma.rest.internal.fee_types.resource.FeeTypeResource;
import com.mma.rest.internal.unit.resource.UnitResource;
import com.mma.rest.internal.users.resource.UserResource;

public class FeeResource {

	private int id;
	private UnitResource unit;
	private UserResource user;
	private FeeTypeResource feeType;
	private Date timeAdded;
	
	public FeeResource() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UnitResource getUnit() {
		return unit;
	}

	public void setUnit(UnitResource unit) {
		this.unit = unit;
	}

	public UserResource getUser() {
		return user;
	}

	public void setUser(UserResource user) {
		this.user = user;
	}

	public FeeTypeResource getFeeType() {
		return feeType;
	}

	public void setFeeType(FeeTypeResource feeType) {
		this.feeType = feeType;
	}

	public Date getTimeAdded() {
		return timeAdded;
	}

	public void setTimeAdded(Date timeAdded) {
		this.timeAdded = timeAdded;
	}
}
