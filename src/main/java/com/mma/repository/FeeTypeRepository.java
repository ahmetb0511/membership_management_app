package com.mma.repository;

import java.util.List;

import com.mma.common.datatable.repository.JpaDataTableRepository;
import com.mma.domain.FeeType;
import com.mma.domain.Unit;

public interface FeeTypeRepository extends JpaDataTableRepository<FeeType, Integer> {

	FeeType findById(int id);
	
	List<FeeType> findByUnit(Unit unit);
}
