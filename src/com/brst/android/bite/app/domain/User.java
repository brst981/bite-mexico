package com.brst.android.bite.app.domain;

public class User {

	private String name;
	private String FirstName;
	private String lastName;
	private String email;
	private String uFacebookId;
	private String customerid;
	private String userSessionId;

	public String getName() {
		return getFirstName() + " " + getLastName();
	}

	public String getFirstName() {
		return FirstName;
	}

	public void setFirstName(String firstName) {
		FirstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getUserSessionId() {
		return userSessionId;
	}

	public void setUserSessionId(String userSessionId) {
		this.userSessionId = userSessionId;
	}

	public String getuFacebookId() {
		return uFacebookId;
	}

	public void setuFacebookId(String uFacebookId) {
		this.uFacebookId = uFacebookId;
	}

}
