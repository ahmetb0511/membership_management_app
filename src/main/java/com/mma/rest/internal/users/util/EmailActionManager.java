package com.mma.rest.internal.users.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.mma.domain.User;
import com.mma.rest.internal.users.service.UserService;
import com.mma.util.MailerService;

@Component
public class EmailActionManager {

	@Autowired
	private HttpServletRequest domainRequest;
	private UserService userService;
	private MailerService mailerService;

	private BCryptPasswordEncoder passwordEncoder;

	public EmailActionManager(UserService userService, MailerService mailerService, BCryptPasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.mailerService = mailerService;
		this.passwordEncoder = passwordEncoder;
	}

	private static final String RESET_PASSWORD_URL = "reset-password/";

	private static final String RESET_PASSWORD_SUBJECT = "Membership Management App Password Reset";


	public void generateResetPassCodeAndSendMail(User user) {
		userService.setVerificationData(user);
		mailerService.send(user.getEmail(), null, RESET_PASSWORD_SUBJECT, buildPassRecoveryEmailContent(user));
	}

	public void resetPassword(String emailToken, String password) {
		String newPassword = passwordEncoder.encode(password);
		userService.resetUserPassword(emailToken, newPassword);
	}

	public String buildChangePasswordEmailContent(User user,String password) {
		StringBuilder sb = new StringBuilder();
		sb
		.append("Dear ").append(",").append(addNewLine()).append(addNewLine())
		.append("We inform you that the password for this account (").append(user.getEmail()).append(") on ").append(user.getUnit().getName()).append(" unit is changed.").append(addNewLine())
		.append("Your new password is: ").append(password).append(addNewLine()).append(addNewLine())
		.append("Kind regards,").append(addNewLine())
		.append("AEK team");		
		return sb.toString();
	}

	public String buildPassRecoveryEmailContent(User user) {
		StringBuilder sb = new StringBuilder();
		sb
		.append("Dear ").append(", ").append(addNewLine()).append(addNewLine())
		.append("We have received a password reset request for this account (").append(user.getEmail()).append(") on ").append(user.getUnit().getName()).append(" unit.").append(addNewLine())
		.append("To reset your password, please follow this link:").append(addNewLine())
		.append(getActionLink(user.getEmailToken())).append(addNewLine()).append(addNewLine())
		.append("Kind regards,").append(addNewLine())
		.append("AEK team");

		return sb.toString();

	}

	protected String addNewLine() {
		return "\r\n";
	} 

	private String getActionLink(String emailToken) {
		StringBuilder link = new StringBuilder();

		link.append(getBaseUrl(domainRequest, false)).append("/");
		link.append(RESET_PASSWORD_URL);
		link.append(emailToken);

		return link.toString();
	}

	/**
	 * Method is trying to determine the base URL of the web application. 
	 * If the application is behind a reverse proxy it can be installed as a root domain or a domain namespace.
	 * It is quite irrelevant does the app is configured to the root of tomcat or application context.
	 * If the proxy is configured to application root (only domain, without namespacea), then in the web.xml should be added
	 * 
	 * 		<context-param>
	 * 			<param-name>preferDomain</param-name>
	 *			<param-value>true</param-value>
	 *		</context-param>
	 * 
	 * @param request
	 * @return
	 */
	public static String getBaseUrl(HttpServletRequest request, boolean withoutCtx) {
		boolean isPreferDomain = false;
		String preferDomain = request.getSession().getServletContext().getInitParameter("preferDomain");
		if(!(preferDomain == null || preferDomain.isEmpty())) {
			isPreferDomain = Boolean.parseBoolean(preferDomain);
		}

		boolean rootContext = request.getRequestURI().equals(request.getServletPath());

		StringBuilder baseUrl = new StringBuilder();
		baseUrl.append(request.getScheme()).append("://");

		if (request.getHeader("x-forwarded-for") != null) {
			if (request.getHeader("x-forwarded-host") != null) {
				baseUrl.append(request.getHeader("x-forwarded-host"));
			} else {
				baseUrl.append(request.getHeader("x-forwarded-server"));
			}

			if (!isPreferDomain && !withoutCtx) {
				baseUrl.append(request.getContextPath()).append("/");
			}
		} else {
			baseUrl.append(request.getHeader("host"));
			if (!rootContext && !withoutCtx) {
				baseUrl.append(request.getContextPath()).append("/");
			}
		}

		return baseUrl.toString();
	}
}
