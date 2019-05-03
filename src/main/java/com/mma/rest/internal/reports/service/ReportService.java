package com.mma.rest.internal.reports.service;

import static com.mma.rest.internal.fees.util.FeeSpecification.feesByUnit;
import static com.mma.rest.internal.fees.util.FeeSpecification.feesByUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableResponse;
import com.mma.domain.Fee;
import com.mma.domain.User;
import com.mma.repository.FeeRepository;
import com.mma.repository.UserRepository;
import com.mma.userdetails.UserPrincipal;

@Service
public class ReportService {
	
	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_MODERATOR = "MODERATOR";
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired FeeRepository feeRepository;

	public DataTableResponse<Fee> getReports(DataTableRequest request, UserPrincipal principal) {
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
