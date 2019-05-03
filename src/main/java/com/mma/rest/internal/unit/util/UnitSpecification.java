package com.mma.rest.internal.unit.util;

import org.springframework.data.jpa.domain.Specification;

import com.mma.common.enums.UnitEnums;
import com.mma.domain.Unit;

public class UnitSpecification {

	public static Specification<Unit> notDeleted() {
		return (root, query, cb) ->
		cb.notEqual(root.get("status"), UnitEnums.Status.DELETED);
	}
}
