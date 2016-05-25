package com.brst.android.bite.app.domain;

public class Plan {

	private String planName;
	private String planId;
	private String price;
	private String subTotalprice;

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSubTotalprice() {
		return subTotalprice;
	}

	public void setSubTotalprice(String subTotalprice) {
		this.subTotalprice = subTotalprice;
	}

}
