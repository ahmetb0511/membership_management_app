package com.mma.rest.internal.users.resource;

import java.util.List;

import com.mma.common.enums.UserEnums;
import com.mma.rest.internal.unit.resource.UnitResource;

public class UserResource {

	private int id;
	private String name;
	private String email;
	private String emailToken;
	private String password;
	private String password2;
	private UserEnums.Role role;
	private UnitResource unit;
	private List<UserRightResource> rights;
	private boolean enabled;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmailToken() {
		return emailToken;
	}

	public void setEmailToken(String emailToken) {
		this.emailToken = emailToken;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public UserEnums.Role getRole() {
		return role;
	}

	public void setRole(UserEnums.Role role) {
		this.role = role;
	}

	public UnitResource getUnit() {
		return unit;
	}

	public void setUnit(UnitResource unit) {
		this.unit = unit;
	}

	public List<UserRightResource> getRights() {
		return rights;
	}

	public void setRights(List<UserRightResource> rights) {
		this.rights = rights;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
