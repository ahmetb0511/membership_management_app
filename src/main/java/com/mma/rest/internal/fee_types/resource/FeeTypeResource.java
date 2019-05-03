package com.mma.rest.internal.fee_types.resource;

import com.mma.rest.internal.unit.resource.UnitResource;

public class FeeTypeResource {
	
	private int id;
	private UnitResource unit;
	private String name;
	private String description;
	private double price;
	
	public FeeTypeResource() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
