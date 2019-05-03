package com.mma.rest.internal.fee_types.util;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.mma.domain.FeeType;
import com.mma.domain.Unit;

public class FeeTypeSpecification {
	
	public static Specification<FeeType> feeTypesByUnit(Unit unit) {
		return (root, query, cb) -> {
			Predicate where  = cb.equal(root.get("unit"), unit);
	
			return where;
		};
	}

}
