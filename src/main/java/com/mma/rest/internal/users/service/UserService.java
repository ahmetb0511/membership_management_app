package com.mma.rest.internal.users.service;

import static com.mma.rest.internal.users.util.UserSpecifications.notDeleted;
import static com.mma.rest.internal.users.util.UserSpecifications.usersByUnit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mma.common.NotFoundException;
import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableResponse;
import com.mma.common.enums.UserEnums;
import com.mma.common.util.DateUtil;
import com.mma.domain.Unit;
import com.mma.domain.User;
import com.mma.domain.UserRight;
import com.mma.repository.UnitRepository;
import com.mma.repository.UserRepository;
import com.mma.repository.UserRightRepository;
import com.mma.repository.UserRoleRepository;
import com.mma.userdetails.UserPrincipal;

@Service
public class UserService {
	
	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_MODERATOR = "MODERATOR";
	public static final String ROLE_USER = "USER";
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository roleRepository;
	
	@Autowired
	private UserRightRepository rightRepository;
	
	@Autowired
	private UnitRepository unitRepository;
	
	public DataTableResponse<User> getUsers(DataTableRequest request, UserPrincipal principal) {
		Specification<User> spec = null;
		if(isAdminUser(null, principal.getId())) {
			spec = notDeleted();
		} else {
			User user = userRepository.findById(principal.getId());
			spec = usersByUnit(user.getUnit());
		}
		
		return userRepository.findAll(request, spec);
	}
	
	@Transactional
	public User getUser(int userId) {
		User user = userRepository.findByIdAndStatusNot(userId, UserEnums.Status.DELETED);
		if (user != null && !user.getRole().getName().equalsIgnoreCase(ROLE_ADMIN)) {
			List<UserRight> userRights = rightRepository.findByUser(user); 
			populateRights(userRights);
			
			userRights.stream().forEach(r -> {
				if (r.getId() == 0) {
					r.setUser(user);
					rightRepository.save(r);
				}
			});			
		}
		
		if(user == null) {
			throw new NotFoundException("User " + userId + " not found.");
		}
		
		return user;
	}
	
	@Transactional
	public User createUser(UserPrincipal principal, User user, int unitId, List<UserRight> rights, String role) {
		if(role.equals(ROLE_ADMIN)) {
			user.setRole(roleRepository.findByName(ROLE_ADMIN));
		} else if(role.equals(ROLE_MODERATOR)) {
			user.setRole(roleRepository.findByName(ROLE_MODERATOR));
		} else {
			user.setRole(roleRepository.findByName(ROLE_USER));
		}	
		
		User loggedUser = userRepository.findOne(principal.getId());

		Unit unit =  unitRepository.findOne(unitId);
		user.setUnit(unit != null ? unit : loggedUser.getUnit()); //  If user is created from user interface, set unit from parent logged user
		user = userRepository.save(user);
		
		if (!role.equals(ROLE_ADMIN) && !role.equals(ROLE_USER)) {
			createUserRights(user, rights);
		}
		
		return user;
	}
	
	@Transactional
	public User updateUser(UserPrincipal principal, User user, int unitId, List<UserRight> rights, String role) {
		if(role.equals(ROLE_ADMIN)) {
			user.setRole(roleRepository.findByName(ROLE_ADMIN));
		} else if(role.equals(ROLE_MODERATOR)) {
			user.setRole(roleRepository.findByName(ROLE_MODERATOR));
		} else {
			user.setRole(roleRepository.findByName(ROLE_USER));
		}	
		
		user.setUnit(unitRepository.findOne(unitId));
		user = userRepository.save(user);
		
		if (role.equals(ROLE_ADMIN)) {
			rightRepository.deleteByUser(user);
		} else {
			createUserRights(user, rights);
		}
		
		return user;
	}
	
	private void createUserRights(User user, List<UserRight> rights) {
		populateRights(rights);
		List<UserRight> userRights = rightRepository.findByUser(user);

		for (UserEnums.Category category : UserEnums.Category.values()) {
			UserRight value = findRight(rights, category).get();
			UserRight right = findRight(userRights, category).orElse(new UserRight(category));

			right.setUser(user);
			right.setView(value.isView());
			right.setEdit(value.isEdit());

			right = rightRepository.save(right);
		}
	}
	
	@Transactional
	public User updateUserAccount(User user) {
		user = userRepository.save(user);		
		return user;
	}
	
	@Transactional
	public void updateStatus(UserPrincipal principal, int userId, boolean enabled) {
		User user = userRepository.findOne(userId);
		if (user != null && user.getStatus() != UserEnums.Status.DELETED) {
			user.setStatus(enabled ? UserEnums.Status.ENABLED : UserEnums.Status.DISABLED);
			userRepository.save(user);
		}
	}
	
	@Transactional
	public void deleteUser(UserPrincipal principal, int userId) {
		User user = userRepository.findOne(userId);
		if (user != null) {
			user.setStatus(UserEnums.Status.DELETED);
			userRepository.save(user);
		}
	}
	
	public void setVerificationData(User user) {
		Date now = Calendar.getInstance().getTime();
		String code = user.getId() + "||" + user.getUnit().getId() + "||" + new SimpleDateFormat(DateUtil.DATE_FORMAT).format(now) + "||" + user.getEmail();

		String token = "";
		for (int i = 0; i < 1000; i++) {
			token = DigestUtils.sha256Hex(token + code);
		}

		user.setEmailToken(token);
		user.setEmailTimeRequested(now);
		userRepository.save(user);
	}
	
	public void resetUserPassword(String emailToken, String password) {
		User user = userRepository.findByEmailToken(emailToken);
		if(user != null) {		
			user.setPassword(password);
			user.setEmailTimeConfirmed(Calendar.getInstance().getTime());
			user.setEmailToken("");
			userRepository.save(user);
		}
	}
	
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public User getUserByEmailAndStatusNotDeleted(String email) {
		return userRepository.findByEmailAndStatusNot(email, UserEnums.Status.DELETED);
	}
	
	public User getUserByEmailToken(String emailToken) {
		return userRepository.findByEmailToken(emailToken);
	}

	public List<UserRight> getUserRights(int userId) {
		User user = userRepository.findByIdAndStatusNot(userId, UserEnums.Status.DELETED);		
		return getUserRights(user);
	}
	
	public List<UserRight> getUserRights(User user) {
		return rightRepository.findByUser(user);
	}
	
	public List<Unit> getUnits() {
		return unitRepository.findAll();
	}
	
	public List<User> getUsers() {
		return userRepository.findByStatus(UserEnums.Status.ENABLED);
	}
	
	protected boolean contains(List<UserRight> rights, UserEnums.Category category) {
		return rights.stream()
				.filter(r -> r.getCategory() == category)
				.findFirst().isPresent();
	}
	
	public Optional<UserRight> findRight(List<UserRight> rights, UserEnums.Category category) {
		return rights.stream()
				.filter(r -> r.getCategory() == category)
				.findFirst();
	}
	
	protected void populateRights(List<UserRight> rights) {
		for (UserEnums.Category category : UserEnums.Category.values()) {
			if (!contains(rights, category)) {
				rights.add(new UserRight(category));
			}
		}
	}
	
	public boolean isAdminUser(User user, int principalId) {	
		if(user == null) {
			user = userRepository.findOne(principalId);
		}
		
		return user.getRole().getName().equals(ROLE_ADMIN);
	}
}
