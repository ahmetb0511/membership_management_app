package com.mma.rest.internal.users.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mma.common.enums.UserEnums;
import com.mma.domain.User;
import com.mma.rest.internal.users.resource.UserResource;
import com.mma.rest.internal.users.service.UserService;
import com.mma.rest.internal.users.util.EmailActionManager;
import com.mma.rest.internal.users.util.UserValidator;

@Controller
public class PasswordController {

	private static Logger log = LoggerFactory.getLogger(PasswordController.class);

	private static int TIME_ACTIVE = 48;

	@Autowired
	private UserService userService;

	@Autowired
	private EmailActionManager emailActionManager;

	@Autowired
	private UserValidator userValidator;

	@PostMapping("/forgot-password")
	public @ResponseBody Map<String, String> forgotPassword(@RequestParam("username") String username) {
		Map<String, String> result = new HashMap<String, String>(1);
		try{
			User user = userService.getUserByEmail(username);
			if(user == null) {				
				log.warn("User with selected username does not exist.");
				result.put("error", "User with selected username does not exist.");
			} else if(user.getStatus() != UserEnums.Status.ENABLED) {
				log.warn("User with selected username is not active.");
				result.put("error", "User with selected username is not active.");
			} else {
				emailActionManager.generateResetPassCodeAndSendMail(user);
				log.info("Email sent to email: " + username);
				result.put("ok", "Email sent to email: " + username);
			}

		} catch (Exception e) {			
			log.error("Error occured with forgot password request.", e);
			result.put("error", "Error while reseting password. Please try again later or contact the administrator");
		}

		return result;
	}

	@GetMapping("/reset-password/{emailToken}")
	public String showResetPasswordForm(@PathVariable("emailToken") String emailToken, Model model) {
		String errorMsg = "";
		try{
			User user = userService.getUserByEmailToken(emailToken);
			if(user == null) {				
				log.warn("User with entered email token does not exist.");
				errorMsg = "User with entered email token does not exist.";
			} else if(user.getStatus() != UserEnums.Status.ENABLED) { 
				log.warn("User with entered email token is not active.");
				errorMsg = "User with entered email token is not active.";
			} else {
				Calendar calendar = Calendar.getInstance();
				Date timeRequested = user.getEmailTimeRequested();
				boolean expired = calendar.getTime().getTime() - timeRequested.getTime() < TIME_ACTIVE * 60 * 60 * 1000 ? false : true;
				if(expired) {
					log.warn("This entered email token has expired.");
					errorMsg = "The password reset mail has expired. Please request again forgot password action from login page.";
				}
			}
		} catch (Exception e) {			
			log.error("Error occured with forgot password request.", e);
			errorMsg = "Error while reseting password. Please try again later or contact the administrator.";
		}

		UserResource userResource = new UserResource();
		userResource.setEmailToken(emailToken);
		model.addAttribute("errorMsg", errorMsg);		
		model.addAttribute("userForm", userResource);

		return "resetPassword";
	}

	@PostMapping("/reset-password/")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public @ResponseBody List<ObjectError> restPasswordConfirm(@ModelAttribute("userForm") UserResource res, Errors errors) {	
		try {
			userValidator.validateResetPassword(res, errors);

			if(!errors.hasErrors()) {
				emailActionManager.resetPassword(res.getEmailToken(), res.getPassword());
				log.info("User password successfully changed [Token: " + res.getEmailToken() + " ].");

			}
		} catch (Exception e) {			
			log.error("Error occured with forgot password request.", e);
			errors.reject("!", "Failed to reset password! Please try again later or contact the administrator");
		}

		return errors.getAllErrors();
	}
	
}
