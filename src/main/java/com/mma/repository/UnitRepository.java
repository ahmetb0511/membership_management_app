package com.mma.repository;

import com.mma.common.datatable.repository.JpaDataTableRepository;
import com.mma.common.enums.UnitEnums.Status;
import com.mma.domain.Unit;

public interface UnitRepository extends JpaDataTableRepository<Unit, Integer> {

	public Unit findById(int id);
	
	public Unit findBySupportEmailAndStatusNot(String supportEmail, Status status);
}
