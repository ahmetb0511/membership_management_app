package com.mma.common.datatable.repository;

import java.util.Arrays;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableRequest.Column;

public class JpaDataTableSpecification<T> implements Specification<T> {
	
	protected final static String OR_SEPARATOR = "+";

	protected final static String ATTRIBUTE_SEPARATOR = ".";

	protected final static char ESCAPE_CHAR = '\\';
	
	private final DataTableRequest request;
	
	public JpaDataTableSpecification(DataTableRequest request) {
		this.request = request;
	}

	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		Predicate predicate = criteriaBuilder.conjunction();
		
		for (Column column : request.getColumns()) {
			String filterValue = column.getSearch().getValue();
			if (column.getSearchable() && StringUtils.hasText(filterValue)) {
				Expression<String> expression = getExpression(root, criteriaBuilder, column.getData());

				if (filterValue.contains(OR_SEPARATOR)) {
					// the filter contains multiple values, add a 'WHERE
					// .. IN' clause
					// Note: "\\" is added to escape special character
					// '+'
					String[] values = filterValue.split("\\" + OR_SEPARATOR);
					if (values.length > 0 && isBoolean(values[0])) {
						Object[] booleanValues = new Boolean[values.length];
						for (int i = 0; i < values.length; i++) {
							booleanValues[i] = Boolean.valueOf(values[i]);
						}
						predicate = criteriaBuilder.and(predicate,
								expression.as(Boolean.class).in(booleanValues));
					} else {
						predicate = criteriaBuilder.and(predicate, expression.in(Arrays.asList(values)));
					}
				} else {
					// the filter contains only one value, add a 'WHERE
					// .. LIKE' clause
					if (isBoolean(filterValue)) {
						predicate = criteriaBuilder.and(predicate, criteriaBuilder
								.equal(expression.as(Boolean.class), Boolean.valueOf(filterValue)));
					} else {
						predicate = criteriaBuilder.and(predicate,
								criteriaBuilder.like(criteriaBuilder.lower(expression),
										getLikeFilterValue(filterValue), ESCAPE_CHAR));
					}
				}
			}
		}

		// check whether a global filter value exists
		String globalFilterValue = request.getSearch().getValue();
		if (StringUtils.hasText(globalFilterValue)) {
			Predicate matchOneColumnPredicate = criteriaBuilder.disjunction();
			// add a 'WHERE .. LIKE' clause on each searchable column
			for (Column column : request.getColumns()) {
				if (column.getSearchable()) {
					String name = column.getName();
					if (!StringUtils.hasText(name)) {
						name = column.getData();
					}

					Expression<String> expression = getExpression(root, criteriaBuilder, name);
					matchOneColumnPredicate = criteriaBuilder.or(matchOneColumnPredicate,
							criteriaBuilder.like(criteriaBuilder.lower(expression),
									getLikeFilterValue(globalFilterValue), ESCAPE_CHAR));
				}
			}
			
			predicate = criteriaBuilder.and(predicate, matchOneColumnPredicate);
		}
		
		return predicate;
	}
	
	protected Expression<String> getExpression(Root<?> root, CriteriaBuilder criteriaBuilder, String columnData) {
		if (columnData.contains(ATTRIBUTE_SEPARATOR)) {
			// columnData is like "joinedEntity.attribute" so add a join clause
			String[] values = columnData.split("\\" + ATTRIBUTE_SEPARATOR);
			if (!root.getModel().getAttribute(values[0]).isAssociation()) {
				return root.get(values[0]).get(values[1]).as(String.class);
			}
			return root.join(values[0], JoinType.LEFT).get(values[1]).as(String.class);
		} else {
			// columnData is like "attribute" so nothing particular to do
			return root.get(columnData).as(String.class);
		}
	}
	
	protected String getLikeFilterValue(String filterValue) {
		return "%" + filterValue.toLowerCase().replaceAll("%", "\\\\" + "%").replaceAll("_", "\\\\" + "_") + "%";
	}
	
	protected boolean isBoolean(String filterValue) {
		return "TRUE".equalsIgnoreCase(filterValue) || "FALSE".equalsIgnoreCase(filterValue);
	}

}
