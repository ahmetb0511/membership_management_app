package com.mma.validation;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

public class ResourceValidatorUtils {

	public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static boolean validateUrl(String url) {
		String resultUrl = url;
		if (url.indexOf("?") != -1) {
			resultUrl = url.substring(0, url.indexOf("?"));
		}

		// This matches the beginning of the URL string against any "word character" followed by a colon (:) and two slashes (//), 
		// and then defaults it to http:// if the protocol part of the URL is missing.
		if (!resultUrl.toLowerCase().matches("^\\w+://.*")) {
			resultUrl = "http://" + resultUrl;
		}

		URL u;
		try {
			u = new URL(resultUrl);
		} catch (MalformedURLException e) {
			return false;
		}

		try {
			u.toURI();
		} catch (URISyntaxException e) {
			return false;
		}

		return true;
	}

	public static boolean checkPassword(String password, String password2, Errors errors) {
		boolean samePasswords = true;

		ValidationUtils.rejectIfEmpty(errors, "password", "required", "Please enter password.");
		ValidationUtils.rejectIfEmpty(errors, "password2", "required", "Please repeat password.");

		if (!password.matches(".{8,16}")) {
			samePasswords = false;
			errors.rejectValue("password", "password", "Password length: {8,16}.");
		}

		if (!password.isEmpty() && !password2.isEmpty()) {
			if (!password.equals(password2)) {
				samePasswords = false;
				errors.rejectValue("password2", "password2", "Passwords do not match.");
			}
		}

		return samePasswords;
	}
}
