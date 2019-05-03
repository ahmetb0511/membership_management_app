package com.mma.rest.internal.fees.util;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.mma.domain.Fee;
import com.mma.domain.Unit;
import com.mma.domain.User;

public class FeeSpecification {
	
	public static Specification<Fee> feesByUnit(Unit unit) {
		return (root, query, cb) -> {
			Predicate where  = cb.equal(root.get("unit"), unit);
	
			return where;
		};
	}

	public static Specification<Fee> feesByUser(User user) {
		return (root, query, cb) -> {
			Predicate where  = cb.equal(root.get("user"), user);
	
			return where;
		};
	}
}
