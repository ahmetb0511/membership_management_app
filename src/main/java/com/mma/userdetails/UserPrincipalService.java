package com.mma.userdetails;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mma.common.enums.UserEnums.Category;
import com.mma.common.enums.UserEnums.Status;
import com.mma.domain.User;
import com.mma.domain.UserRight;
import com.mma.repository.UserRepository;
import com.mma.repository.UserRightRepository;

@Service
public class UserPrincipalService implements UserDetailsService {

	public enum Role {

		ADMINISTRATOR,
		MODERATOR,
		USER;

		private GrantedAuthority auth;

		GrantedAuthority authority() {
			if (auth == null) {
				auth = new SimpleGrantedAuthority("ROLE_" + name());
			}

			return auth;
		}
	}

	public enum Right {

		VIEW_USERS, 
		EDIT_USERS, 
		VIEW_REPORTS,
		VIEW_FEES,
		EDIT_FEES,
		VIEW_FEE_TYPES,
		EDIT_FEE_TYPES;

		private GrantedAuthority auth;

		GrantedAuthority authority() {
			if (auth == null) {
				auth = new SimpleGrantedAuthority("ROLE_" + name());
			}

			return auth;
		}
	}

	private UserRepository userRepository;
	private UserRightRepository rightRepository;

	public UserPrincipalService(UserRepository userRepository, UserRightRepository rightRepository) {
		this.userRepository = userRepository;
		this.rightRepository = rightRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmailAndStatusNot(username, Status.DELETED);
		if (user == null) {
			throw new UsernameNotFoundException("User " + username + " not found.");
		}

		return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), user.getStatus() == Status.ENABLED, getAuthorities(user));
	}

	protected List<GrantedAuthority> getAuthorities(User user) {
		List<GrantedAuthority> authorities = new ArrayList<>();

		if (user.getRole() != null && user.getRole().getName().equalsIgnoreCase("ADMIN")) {
			authorities.add(Role.ADMINISTRATOR.authority());
			authorities.add(Right.VIEW_USERS.authority());
			authorities.add(Right.EDIT_USERS.authority());
			authorities.add(Right.VIEW_REPORTS.authority());
			authorities.add(Right.VIEW_FEES.authority());
			authorities.add(Right.EDIT_FEES.authority());
			authorities.add(Right.VIEW_FEE_TYPES.authority());
			authorities.add(Right.EDIT_FEE_TYPES.authority());
		} else if (user.getRole() != null && user.getRole().getName().equalsIgnoreCase("MODERATOR")) {
			authorities.add(Role.MODERATOR.authority());

			for (UserRight ur : rightRepository.findByUser(user)) {
				if (ur.getCategory() == Category.REPORTS) {
					if (ur.isView()) {
						authorities.add(Right.VIEW_REPORTS.authority());
					}
				} else if (ur.getCategory() == Category.USERS) {
					if (ur.isView()) {
						authorities.add(Right.VIEW_USERS.authority());
					}
					if (ur.isEdit()) {
						authorities.add(Right.EDIT_USERS.authority());
					}
				} else if (ur.getCategory() == Category.FEES) {
					if (ur.isView()) {
						authorities.add(Right.VIEW_FEES.authority());
					}
					if (ur.isEdit()) {
						authorities.add(Right.EDIT_FEES.authority());
					}
				} else if (ur.getCategory() == Category.FEE_TYPES) {
					if (ur.isView()) {
						authorities.add(Right.VIEW_FEE_TYPES.authority());
					}
					if (ur.isEdit()) {
						authorities.add(Right.EDIT_FEE_TYPES.authority());
					}
				}
			}
		} else {
			authorities.add(Role.USER.authority());

			for (UserRight ur : rightRepository.findByUser(user)) {
				if (ur.getCategory() == Category.REPORTS) {
					if (ur.isView()) {
						authorities.add(Right.VIEW_REPORTS.authority());
					}
				} else if (ur.getCategory() == Category.FEES) {
					if (ur.isView()) {
						authorities.add(Right.VIEW_FEES.authority());
					}
					if (ur.isEdit()) {
						authorities.add(Right.EDIT_FEES.authority());
					}
				}
			}
		}

		return authorities;
	}
}
