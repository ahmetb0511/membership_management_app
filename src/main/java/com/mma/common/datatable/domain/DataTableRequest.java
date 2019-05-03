package com.mma.common.datatable.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTableRequest {

	private Integer draw;
	private Integer start;
	private Integer length;
	private Search search;
	
	private List<Order> order;
	private List<Column> columns;

	public DataTableRequest() {
		this.search = new Search();
		this.order = new ArrayList<Order>();
		this.columns = new ArrayList<Column>();
	}

	public Integer getDraw() {
		return draw;
	}

	public void setDraw(Integer draw) {
		this.draw = draw;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public List<Order> getOrder() {
		return order;
	}

	public void setOrder(List<Order> order) {
		this.order = order;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public Map<String, Column> getColumnsAsMap() {
		Map<String, Column> map = new HashMap<String, Column>();
		for (Column column : columns) {
			map.put(column.getData(), column);
		}
		return map;
	}

	@Override
	public String toString() {
		return "DataTableRequest [draw=" + draw + ", start=" + start + ", length=" + length + ", search="
				+ search + ", order=" + order + ", columns=" + columns + "]";
	}

	public static class Order {

		private Integer column;
		private String dir;

		public Order() {
			
		}

		public Order(Integer column, String dir) {
			super();
			this.column = column;
			this.dir = dir;
		}

		public Integer getColumn() {
			return column;
		}

		public void setColumn(Integer column) {
			this.column = column;
		}

		public String getDir() {
			return dir;
		}

		public void setDir(String dir) {
			this.dir = dir;
		}

		@Override
		public String toString() {
			return "Order [column=" + column + ", dir=" + dir + "]";
		}

	}

	public static class Column {

		private String data;
		private String name;
		private Boolean searchable;
		private Boolean orderable;
		private Search search;

		public Column() {

		}

		public Column(String data, String name, Boolean searchable, Boolean orderable, Search search) {
			super();
			this.data = data;
			this.name = name;
			this.searchable = searchable;
			this.orderable = orderable;
			this.search = search;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Boolean getSearchable() {
			return searchable;
		}

		public void setSearchable(Boolean searchable) {
			this.searchable = searchable;
		}

		public Boolean getOrderable() {
			return orderable;
		}

		public void setOrderable(Boolean orderable) {
			this.orderable = orderable;
		}

		public Search getSearch() {
			return search;
		}

		public void setSearch(Search search) {
			this.search = search;
		}

		@Override
		public String toString() {
			return "Column [data=" + data + ", name=" + name + ", searchable=" + searchable
					+ ", orderable=" + orderable + ", search=" + search + "]";
		}
	}

	public static class Search {

		private String value;
		private Boolean regex;

		public Search() {

		}

		public Search(String value, Boolean regex) {
			super();
			this.value = value;
			this.regex = regex;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Boolean getRegex() {
			return regex;
		}

		public void setRegex(Boolean regex) {
			this.regex = regex;
		}

		@Override
		public String toString() {
			return "Search [value=" + value + ", regex=" + regex + "]";
		}
	}

}
