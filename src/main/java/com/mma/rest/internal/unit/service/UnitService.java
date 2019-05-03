package com.mma.rest.internal.unit.service;

import static com.mma.rest.internal.unit.util.UnitSpecification.notDeleted;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mma.common.NotFoundException;
import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableResponse;
import com.mma.common.enums.UnitEnums;
import com.mma.domain.Unit;
import com.mma.repository.UnitRepository;

@Service
public class UnitService {

	@Autowired
	private UnitRepository unitRepository;
	
	
	public DataTableResponse<Unit> getUnits(DataTableRequest request) {	
		Specification<Unit> spec = notDeleted();
		return unitRepository.findAll(request, spec);
	}
	
	@Transactional
	public Unit getUnit(int unitId) {
		Unit unit = unitRepository.findById(unitId);		
		
		if(unit == null) {
			throw new NotFoundException("Unit " + unitId + " not found.");
		}
		
		return unit;
	}
	
	@Transactional
	public Unit createUnit(Unit unit) {
		
		unit = unitRepository.save(unit);
				
		return unit;
	}
	
	@Transactional
	public Unit updateUnit(Unit unit) {
		
		unit = unitRepository.save(unit);
		
		return unit;
	}
	
	@Transactional
	public void updateStatus(int unitId, boolean enabled) {
		Unit unit = unitRepository.findOne(unitId);
		if (unit != null && unit.getStatus() != UnitEnums.Status.DELETED) {
			unit.setStatus(enabled ? UnitEnums.Status.ACTIVE : UnitEnums.Status.BLOCKED);
			unitRepository.save(unit);
		}
	}
	
	@Transactional
	public void deleteUnit(int unitId) {
		Unit unit = unitRepository.findOne(unitId);
		if (unit != null) {			
			unit.setStatus(UnitEnums.Status.DELETED);
			unitRepository.save(unit);
		}
	}
	
	public Unit getUnitBySupportEmailAndStatusNotDeleted(String supportEmail) {
		return unitRepository.findBySupportEmailAndStatusNot(supportEmail, UnitEnums.Status.DELETED);
	}
}
