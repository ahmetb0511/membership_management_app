package com.mma.common.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;

import com.mma.common.datatable.domain.DataTableResponse;

public interface ResourceConverter<S, T> extends Converter<S, T> {

	default List<T> convertList(Iterable<S> iterable) {
		List<T> list = new ArrayList<>();
		iterable.forEach(source -> list.add(convert(source)));
		return list;
	}

	default DataTableResponse<T> convertResponse(DataTableResponse<S> source) {
		List<T> data = convertList(source.getData());

		DataTableResponse<T> target = new DataTableResponse<>();
		target.setDraw(source.getDraw());
		target.setRecordsFiltered(source.getRecordsFiltered());
		target.setRecordsTotal(source.getRecordsTotal());
		target.setData(data);

		return target;
	}
	
}
