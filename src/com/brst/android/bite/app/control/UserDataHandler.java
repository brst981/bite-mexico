package com.brst.android.bite.app.control;

import com.brst.android.bite.app.domain.User;

public class UserDataHandler {

	public static UserDataHandler uInstance;

	public User user;

	public String userSelectedPlan;

	/* Static 'instance' method */
	public static UserDataHandler getInstance() {
		return uInstance;
	}

	public static void initInstance() {
		if (uInstance == null) {
			uInstance = new UserDataHandler();
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUserSelectedPlan() {
		return userSelectedPlan;
	}

	public void setUserSelectedPlan(String userSelectedPlan) {
		this.userSelectedPlan = userSelectedPlan;
	}

}
