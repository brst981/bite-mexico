package com.brst.android.bite.app.control;

import com.brst.android.bite.app.domain.Restaurant;

public class RestaurantDataHandler {

	private static RestaurantDataHandler resInstance;

	public Restaurant restaurant;

	/* Static 'instance' method */
	public static RestaurantDataHandler getInstance() {
		return resInstance;
	}

	public static void initInstance() {
		if (resInstance == null) {
			resInstance = new RestaurantDataHandler();
		}
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

}
