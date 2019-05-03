package com.mma.rest.internal.fees.service;

import static com.mma.rest.internal.fees.util.FeeSpecification.feesByUnit;
import static com.mma.rest.internal.fees.util.FeeSpecification.feesByUser;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableResponse;
import com.mma.common.enums.UserEnums;
import com.mma.domain.Fee;
import com.mma.domain.FeeType;
import com.mma.domain.Unit;
import com.mma.domain.User;
import com.mma.repository.FeeRepository;
import com.mma.repository.FeeTypeRepository;
import com.mma.repository.UnitRepository;
import com.mma.repository.UserRepository;
import com.mma.rest.internal.fees.resource.FeeResource;
import com.mma.rest.internal.fees.resource.UserResource;
import com.mma.userdetails.UserPrincipal;

@Service
public class FeeService {
	
	static final Logger LOGGER = LoggerFactory.getLogger(FeeService.class);

	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_MODERATOR = "MODERATOR";
	
	@Autowired
	private Collator collator;
	
	@Autowired
	private FeeRepository feeRepository;
	
	@Autowired
	private FeeTypeRepository feeTypeRepository;
	
	@Autowired
	private UnitRepository unitRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public DataTableResponse<Fee> getFees(DataTableRequest request, UserPrincipal principal) {
		Specification<Fee> spec = null;
		if(isAdminUser(null, principal.getId())) {
			spec = null;
		} else if(isModeratorUser(null, principal.getId())) {
			User user = userRepository.findById(principal.getId());
			spec = feesByUnit(user.getUnit());
		} else {
			User user = userRepository.findById(principal.getId());
			spec = feesByUser(user);
		}
		
		return feeRepository.findAll(request, spec);
	}
	
	@Transactional
	public Fee createFee(UserPrincipal principal, Fee fee, int unitId, FeeResource resource) {		
		User loggedUser = userRepository.findOne(principal.getId());

		Unit unit =  unitRepository.findOne(unitId);
		fee.setUnit(unit != null ? unit : loggedUser.getUnit()); //  If fee is created from moderator interface, set unit from logged user
		
		User user =  userRepository.findById(resource.getUser().getId());
		fee.setUser(user);
		
		FeeType feeType =  feeTypeRepository.findById(resource.getFeeType().getId());
		fee.setFeeType(feeType);
		
		fee = feeRepository.save(fee);
		
		return fee;
	}
	
	@Transactional
	public Fee updateFee(UserPrincipal principal, Fee fee, int unitId, FeeResource resource) {
		fee.setUnit(unitRepository.findOne(unitId));
		fee.setUser(userRepository.findOne(resource.getUser().getId()));
		fee.setFeeType(feeTypeRepository.findOne(resource.getFeeType().getId()));
		fee = feeRepository.save(fee);
		
		return fee;
	}
	
	public List<UserResource> getUsersByUnit(int unitId) {
		LOGGER.debug("Getting users by unit [{}]...", unitId);
		List<UserResource> result = new ArrayList<>();

		try {
			Unit u = unitRepository.findById(unitId);
			List<User> users = userRepository.findByUnitAndStatus(u, UserEnums.Status.ENABLED);
			for (User user : users) {
				result.add(new UserResource(Integer.toString(user.getId()), user.getName()));
			}

			// sort by name
			Collections.sort(result, (UserResource o1, UserResource o2) -> collator.compare(o1.getName(), o2.getName()));
		} catch (Exception e) {
			LOGGER.error("Getting users failed", e);
		}

		return result;
	}
	
	public boolean isAdminUser(User user, int principalId) {	
		if(user == null) {
			user = userRepository.findOne(principalId);
		}
		
		return user.getRole().getName().equals(ROLE_ADMIN);
	}
	
	public boolean isModeratorUser(User user, int principalId) {	
		if(user == null) {
			user = userRepository.findOne(principalId);
		}
		
		return user.getRole().getName().equals(ROLE_MODERATOR);
	}
}
