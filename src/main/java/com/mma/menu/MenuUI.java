package com.mma.menu;

import org.springframework.stereotype.Component;

@Component("menuUI")
public class MenuUI {

	public String itemClass(String active, String current) {
		if (active.equals(current)) {
			return "active";
		}

		return "";
	}
	
}
