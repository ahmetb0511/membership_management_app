package com.mma.rest.internal.users.util;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.mma.common.enums.UserEnums;
import com.mma.domain.Unit;
import com.mma.domain.User;

public class UserSpecifications {

	public static Specification<User> notDeleted() {
		return (root, query, cb) ->
		cb.notEqual(root.get("status"), UserEnums.Status.DELETED);
	}
	
	public static Specification<User> usersByUnit(Unit unit) {
		return (root, query, cb) -> {
			Predicate where  = cb.notEqual(root.get("status"), UserEnums.Status.DELETED);

			where = cb.and(where, cb.equal(root.get("unit"), unit));
	
			
			return where;
		};
	}

}
