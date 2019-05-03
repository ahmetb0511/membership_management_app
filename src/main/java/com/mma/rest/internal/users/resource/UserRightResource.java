package com.mma.rest.internal.users.resource;

import com.mma.common.enums.UserEnums.Category;

public class UserRightResource {

	private Category category;
	private boolean view;
	private boolean edit;
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public boolean isView() {
		return view;
	}
	
	public void setView(boolean view) {
		this.view = view;
	}
	
	public boolean isEdit() {
		return edit;
	}
	
	public void setEdit(boolean edit) {
		this.edit = edit;
	}
}
