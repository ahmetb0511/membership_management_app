package com.mma.common.datatable.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.StringUtils;

import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableResponse;

public class JpaDataTableRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements JpaDataTableRepository<T, ID> {

	public JpaDataTableRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
	}

	public DataTableResponse<T> findAll(DataTableRequest request) {
		return findAll(request, null);
	}

	public DataTableResponse<T> findAll(DataTableRequest request, Specification<T> specification) {
		DataTableResponse<T> response = new DataTableResponse<T>();
		response.setDraw(response.getDraw());
		
		long count = specification == null ? count() : count(specification);
		if (count > 0) {
			Specifications<T> query = Specifications.where(new JpaDataTableSpecification<T>(request));
			if (specification != null) {
				query = query.and(specification);
			}
			
			Page<T> data = findAll(query, toPageable(request));
			response.setData(data.getContent());
			response.setRecordsFiltered(data.getTotalElements());
			response.setRecordsTotal(count);
		}
		
		return response;
	}
	
	protected Pageable toPageable(DataTableRequest request) {
		List<Order> orders = new ArrayList<Order>();
		for (DataTableRequest.Order order : request.getOrder()) {
			DataTableRequest.Column column = request.getColumns().get(order.getColumn());
			if (column.getOrderable()) {
				String sortColumn = column.getName();
				if (!StringUtils.hasText(sortColumn)) {
					sortColumn = column.getData();
				}
				Direction sortDirection = Direction.fromString(order.getDir());
				orders.add(new Order(sortDirection, sortColumn));
			}
		}
		
		Sort sort = orders.isEmpty() ? null : new Sort(orders);
		if (request.getLength() == -1) {
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
		}
		
		return new PageRequest(request.getStart() / request.getLength(), request.getLength(), sort);
	}
	
}
