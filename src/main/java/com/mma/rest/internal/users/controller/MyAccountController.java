package com.mma.rest.internal.users.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.mma.domain.User;
import com.mma.menu.Item;
import com.mma.menu.MenuController;
import com.mma.rest.internal.users.resource.UserConverter;
import com.mma.rest.internal.users.resource.UserResource;
import com.mma.rest.internal.users.service.UserService;
import com.mma.userdetails.CurrentUser;
import com.mma.userdetails.UserPrincipal;

@MenuController(value = "/myaccount", item = Item.Users)
public class MyAccountController {

	private static Logger log = LoggerFactory.getLogger(MyAccountController.class);
	
	private UserService userService;
	private UserConverter userConverter;	

	public MyAccountController(UserService userService, UserConverter userConverter) {
		this.userService = userService;
		this.userConverter = userConverter;
	}

	@GetMapping
	public String getMyAccountData(@CurrentUser UserPrincipal principal, Model model) {
		User user;
		try {
			user = userService.getUser(principal.getId());
			UserResource resource = userConverter.convert(user);
			return showPage(principal, resource, model);
		} catch (Exception e) {
			log.error("Error occured while getting my account data.", e);
		}

		return null;
	}

	@PostMapping
	public String updateMyAccount(@CurrentUser UserPrincipal principal, @ModelAttribute("userForm") @Validated UserResource resource, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showPage(principal, resource, model);
		}
		
		return saveUser(principal, principal.getId(), resource);
	}

	protected String showPage(UserPrincipal principal, UserResource user, Model model) {
		model.addAttribute("userForm", user);
		model.addAttribute("unitList", userService.getUnits());

		return "users/myaccount";
	}

	protected String saveUser(UserPrincipal principal, int userId, UserResource resource) {
		User user = userService.getUser(userId);
		user.setName(resource.getName());

		userService.updateUserAccount(user);

		return "redirect:/";
	}

}
