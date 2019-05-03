package com.mma.rest.internal.fee_types.service;

import static com.mma.rest.internal.fee_types.util.FeeTypeSpecification.feeTypesByUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableResponse;
import com.mma.domain.FeeType;
import com.mma.domain.Unit;
import com.mma.domain.User;
import com.mma.repository.FeeTypeRepository;
import com.mma.repository.UnitRepository;
import com.mma.repository.UserRepository;
import com.mma.userdetails.UserPrincipal;

@Service
public class FeeTypeService {

	public static final String ROLE_ADMIN = "ADMIN";
	
	@Autowired
	private FeeTypeRepository feeTypeRepository;
	
	@Autowired
	private UnitRepository unitRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public DataTableResponse<FeeType> getFeeTypes(DataTableRequest request, UserPrincipal principal) {
		Specification<FeeType> spec = null;
		if(!isAdminUser(null, principal.getId())) {
			User user = userRepository.findById(principal.getId());
			spec = feeTypesByUnit(user.getUnit());
		}
		
		return feeTypeRepository.findAll(request, spec);
	}
	
	@Transactional
	public FeeType createFeeType(UserPrincipal principal, FeeType feeType, int unitId) {		
		User loggedUser = userRepository.findOne(principal.getId());

		Unit unit =  unitRepository.findOne(unitId);
		feeType.setUnit(unit != null ? unit : loggedUser.getUnit()); //  If fee type is created from moderator interface, set unit from logged user
		feeType = feeTypeRepository.save(feeType);
		
		return feeType;
	}
	
	@Transactional
	public FeeType updateFeeType(UserPrincipal principal, FeeType feeType, int unitId) {
		feeType.setUnit(unitRepository.findOne(unitId));
		feeType = feeTypeRepository.save(feeType);
		
		return feeType;
	}
	
	public boolean isAdminUser(User user, int principalId) {	
		if(user == null) {
			user = userRepository.findOne(principalId);
		}
		
		return user.getRole().getName().equals(ROLE_ADMIN);
	}
}
