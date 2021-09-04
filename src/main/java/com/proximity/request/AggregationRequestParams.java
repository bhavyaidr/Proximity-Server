package com.proximity.request;

public class AggregationRequestParams {
	private String aggField;
	private String aggName;
	
	public String getAggName() {
		return aggName;
	}

	public void setAggName(String aggName) {
		this.aggName = aggName;
	}

	public String getAggField() {
		return aggField;
	}

	public void setAggField(String aggField) {
		this.aggField = aggField;
	}
}
