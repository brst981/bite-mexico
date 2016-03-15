package com.brst.android.bite.app.domain;

public class Rating {

	private String ratingBy;
	private String quotes;
	private int ratingValue;
	private String ratingDate;

	public String getRatingBy() {
		return ratingBy;
	}

	public void setRatingBy(String ratingBy) {
		this.ratingBy = ratingBy;
	}

	public String getQuotes() {
		return quotes;
	}

	public void setQuotes(String quotes) {
		this.quotes = quotes;
	}

	public int getRatingValue() {
		return ratingValue;
	}

	public void setRatingValue(int ratingValue) {
		this.ratingValue = ratingValue;
	}

	public String getRatingDate() {
		return ratingDate;
	}

	public void setRatingDate(String ratingDate) {
		this.ratingDate = ratingDate;
	}

}
