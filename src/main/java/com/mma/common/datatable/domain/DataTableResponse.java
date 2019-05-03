package com.mma.common.datatable.domain;

import java.util.Collections;
import java.util.List;

public class DataTableResponse<T> {
	
	private Integer draw;
	private Long recordsTotal = 0L;
	private Long recordsFiltered = 0L;
	
	private List<T> data = Collections.emptyList();
	private String error;
	
	public interface View {
		
	}
	
	public Integer getDraw() {
		return draw;
	}
	
	public void setDraw(Integer draw) {
		this.draw = draw;
	}
	
	public Long getRecordsTotal() {
		return recordsTotal;
	}
	
	public void setRecordsTotal(Long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	
	public Long getRecordsFiltered() {
		return recordsFiltered;
	}
	
	public void setRecordsFiltered(Long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	
	public List<T> getData() {
		return data;
	}
	
	public void setData(List<T> data) {
		this.data = data;
	}
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	@Override
	public String toString() {
		return "DataTableResponse [draw=" + draw + ", recordsTotal=" + recordsTotal
				+ ", recordsFiltered=" + recordsFiltered + ", error=" + error + "]";
	}

}
