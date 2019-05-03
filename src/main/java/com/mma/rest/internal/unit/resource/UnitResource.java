package com.mma.rest.internal.unit.resource;

public class UnitResource { 

	private int id;
	private String name;
	private String supportEmail;
	private boolean enabled;

	public UnitResource() {
		super();
	}

	public UnitResource(int id, String name, String supportEmail, boolean enabled) {
		this.id = id;
		this.name = name;
		this.supportEmail = supportEmail;
		this.enabled = enabled;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSupportEmail() {
		return supportEmail;
	}

	public void setSupportEmail(String supportEmail) {
		this.supportEmail = supportEmail;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
