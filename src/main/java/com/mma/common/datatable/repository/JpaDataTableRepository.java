package com.mma.common.datatable.repository;

import java.io.Serializable;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableResponse;

@NoRepositoryBean
public interface JpaDataTableRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
	
	DataTableResponse<T> findAll(DataTableRequest request);

	DataTableResponse<T> findAll(DataTableRequest request, Specification<T> specification);

}
