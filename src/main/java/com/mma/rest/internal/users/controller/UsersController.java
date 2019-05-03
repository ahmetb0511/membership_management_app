package com.mma.rest.internal.users.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableResponse;
import com.mma.common.enums.UserEnums;
import com.mma.common.enums.UserEnums.Role;
import com.mma.domain.User;
import com.mma.domain.UserRight;
import com.mma.menu.Item;
import com.mma.menu.MenuController;
import com.mma.repository.UserRepository;
import com.mma.repository.UserRoleRepository;
import com.mma.rest.internal.users.resource.UserConverter;
import com.mma.rest.internal.users.resource.UserResource;
import com.mma.rest.internal.users.resource.UserRightConverter;
import com.mma.rest.internal.users.resource.UserRightResource;
import com.mma.rest.internal.users.service.UserService;
import com.mma.rest.internal.users.util.EmailActionManager;
import com.mma.userdetails.CurrentUser;
import com.mma.userdetails.UserPrincipal;
import com.mma.util.MailerService;

@MenuController(value = "/users", item = Item.Users)
public class UsersController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private UserConverter userConverter;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository roleRepository;
	
	@Autowired
	private UserRightConverter rightConverter;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailerService mailerService;
	
	@Autowired
	private EmailActionManager emailActionManager;
	
	@GetMapping
	public String showPage(@CurrentUser UserPrincipal principal) {	
		if(userService.isAdminUser(null, principal.getId())) {
			return "users/admin-users";
		}
		return "users/unit-users";
	}
	
	@PostMapping("/data")
	public @ResponseBody DataTableResponse<UserResource> getUsers(@RequestBody DataTableRequest request, @CurrentUser UserPrincipal principal) {		
		return userConverter.convertResponse(userService.getUsers(request, principal));
	}
	
	@DeleteMapping("/{userIds}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUsers(@CurrentUser UserPrincipal principal, @PathVariable Integer[] userIds) {
		for (Integer userId: userIds) {
			userService.deleteUser(principal, userId);
		}
	}
	
	@PutMapping("/{userIds}/disable")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void disableUsers(@CurrentUser UserPrincipal principal, @PathVariable Integer[] userIds) {
		changeStatus(principal, userIds, false);
	}
	
	@PutMapping("/{userIds}/enable")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void enableUsers(@CurrentUser UserPrincipal principal, @PathVariable Integer[] userIds) {
		changeStatus(principal, userIds, true);
	}
	
	protected void changeStatus(UserPrincipal principal, Integer[] userIds, boolean enabled) {
		for (Integer userId: userIds) {
			userService.updateStatus(principal, userId, enabled);
		}
	}
	
	@GetMapping("/newUser")
	public String newUser(@CurrentUser UserPrincipal principal, Model model) {
		UserResource user = new UserResource();
		boolean isAdmin = userService.isAdminUser(null, principal.getId());	
		user.setRole(UserEnums.Role.User);
		user.setEnabled(true);
		user.setRights(Arrays.stream(UserEnums.Category.values())
				.filter(c -> {
					if(!isAdmin && c.name() == UserEnums.Category.USERS.name()) {
						return false;
					}
					return true;})
				.map(c -> {
					UserRightResource r = new UserRightResource();
					r.setCategory(c);
					return r;
				})
				.collect(Collectors.toList())
				);

		return showPage(principal, user, model);
	}

	protected String showPage(UserPrincipal principal, UserResource user, Model model) {
		model.addAttribute("userForm", user);
		model.addAttribute("unitList", userService.getUnits());

		if(userService.isAdminUser(null, principal.getId())) {
			return "users/admin-user-edit";
		}
		return "users/unit-user-edit";
	}

	@ModelAttribute("roleList")
	public UserEnums.Role[] getRoles() {
		return UserEnums.Role.values();
	}

	@PostMapping("/newUser")
	public String createUser(@CurrentUser UserPrincipal principal, @ModelAttribute("userForm") @Validated UserResource resource, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showPage(principal, resource, model);
		}

		return saveUser(principal, 0, resource);
	}

	@GetMapping("/{userId}")
	public String editUser(@CurrentUser UserPrincipal principal, @PathVariable int userId, Model model) {
		User user = userService.getUser(userId);
		boolean isAdmin = userService.isAdminUser(null, principal.getId());	
		UserResource res = userConverter.convert(user);
		res.setRights(userService.getUserRights(user).stream()
				.filter(p -> {
					//Users menu-category should be set only for ParentUsers which is done in adminGUI
					if(!isAdmin && p.getCategory().name() == UserEnums.Category.USERS.name()) {
						return false;
					}				
					return true;})
				.map(p -> rightConverter.convert(p))
				.collect(Collectors.toList())
				);

		return showPage(principal, res, model);
	}

	@PutMapping("/{userId}/changePassword")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public String changeUserPassword(@CurrentUser UserPrincipal principal, @PathVariable int userId, @RequestParam("password") String password) {
		User user = userService.getUser(userId);
		String previousPassword = user.getPassword();
		String newPassword = passwordEncoder.encode(password);

		// if the new password is different from the old one, change it and send email to user 
		if(!previousPassword.equals(newPassword)) {
			user.setPassword(newPassword);	
			userService.updateUserAccount(user);		
			String emailContent = emailActionManager.buildChangePasswordEmailContent(user, password);
			mailerService.send(user.getEmail(),null,"Changed Password", emailContent);
		}
		return "redirect:/users";
	}

	@PostMapping("/{userId}")
	public String updateUser(@CurrentUser UserPrincipal principal, @PathVariable int userId, @ModelAttribute("userForm") @Validated UserResource resource, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showPage(principal, resource, model);
		}

		return saveUser(principal, userId, resource);
	}

	protected String saveUser(UserPrincipal principal, int userId, UserResource resource) {
		User user = populateUser(userId, resource);
		List<UserRight> rights = populateRights(resource);

		
		boolean admin = isAdmin(resource);
		boolean moderator = isModerator(resource);
		boolean principalModerator = isPrincipalModerator(principal);
		int unitId;
		if(principalModerator) {
			User u = userRepository.findById(principal.getId());
			unitId = u.getUnit().getId();
		} else {
			unitId = getUnitId(resource);
		}
		
		if (userId == 0) {
			if(admin) {
				userService.createUser(principal, user, unitId, rights, "ADMIN");
			} else if(moderator) {
				userService.createUser(principal, user, unitId, rights, "MODERATOR");
			} else {
				userService.createUser(principal, user, unitId, rights, "USER");
			}
		} else {
			if(admin) {
				userService.updateUser(principal, user, unitId, rights, "ADMIN");
			} else if(moderator) {
				userService.updateUser(principal, user, unitId, rights, "MODERATOR");
			} else {
				userService.updateUser(principal, user, unitId, rights, "USER");
			}
		}

		return "redirect:/users";
	}

	protected User populateUser(int userId, UserResource resource) {
		User user = null;
		if (userId == 0) {
			user = new User();
			user.setEmail(resource.getEmail());
			user.setPassword(passwordEncoder.encode(resource.getPassword()));
			user.setTimeAdded(new Date());
		} else {
			user = userService.getUser(userId);
		}

		user.setName(resource.getName());
		user.setStatus(resource.isEnabled() ? UserEnums.Status.ENABLED : UserEnums.Status.DISABLED);

		return user;
	}

	protected List<UserRight> populateRights(UserResource resource) {
		if (resource.getRights() != null) {
			return resource.getRights().stream()
					.map(r -> {
						UserRight right = new UserRight();
						right.setCategory(UserEnums.Category.valueOf(r.getCategory().name()));
						right.setView(r.isView());
						right.setEdit(r.isEdit());
						return right;
					})
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	protected int getUnitId(UserResource resource) {
		return resource.getUnit() != null ? resource.getUnit().getId() : 0;
	}

	protected boolean isAdmin(UserResource res) {
		return res.getRole() == Role.Administrator;
	}
	
	protected boolean isModerator(UserResource res) {
		return res.getRole() == Role.Moderator;
	}
	
	protected boolean isPrincipalModerator(UserPrincipal principal) {
		User u = userRepository.findById(principal.getId());
		return u.getRole().equals(roleRepository.findByName("MODERATOR"));
	}
}
