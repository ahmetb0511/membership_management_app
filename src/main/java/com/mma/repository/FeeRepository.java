package com.mma.repository;

import com.mma.common.datatable.repository.JpaDataTableRepository;
import com.mma.domain.Fee;

public interface FeeRepository extends JpaDataTableRepository<Fee, Integer> {

	Fee findById(int id);
}
