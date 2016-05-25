package com.brst.android.bite.app.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.brst.android.bite.app.domain.Plan;
import com.brst.android.bite.app.domain.Rating;
import com.brst.android.bite.app.domain.Restaurant;
import com.google.android.gms.internal.el;

public class JsonParser {

	private static final String TAG = "JSONParser handler";

	public static List<Restaurant> getRestaurantsDetail(String strJson) {

		List<Restaurant> listRestaurants = null;
		try {
			JSONObject jsonOBject = new JSONObject(strJson);

			if (jsonOBject.getBoolean("success")) {
				listRestaurants = new ArrayList<Restaurant>();

				JSONArray jArray = jsonOBject.getJSONArray("products");

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jsonOBjectProduct = jArray.getJSONObject(i);
					Restaurant res = new Restaurant();
					if (jsonOBjectProduct.has("rating")&& jsonOBjectProduct.getInt("rating")!=0) {
						res.setRating(jsonOBjectProduct.getInt("rating"));
					}
				
					JSONObject jsonObjectDetail = jsonOBjectProduct
							.getJSONObject("detail");
					if (jsonObjectDetail.getString("name") != null) {
						res.setName(jsonObjectDetail.getString("name")
								.toUpperCase(Locale.getDefault()));
					}

					if (jsonObjectDetail.getString("entity_id") != null) {
						res.setId(jsonObjectDetail.getString("entity_id"));
					}

					if (jsonObjectDetail.getString("description") != null) {
						res.setDescription(jsonObjectDetail
								.getString("description"));
					}
					if (jsonObjectDetail.getString("address") != null) {
						res.setAddress(jsonObjectDetail.getString("address"));
					}

					if (jsonObjectDetail.getString("cuisine") != null) {
						res.setCuisine(jsonObjectDetail.getString("cuisine")
								.trim());
					}

					if (!jsonObjectDetail.isNull("offer_available")
							&& jsonObjectDetail.getString("offer_available") != null) {
						res.setOfferAvailable(jsonObjectDetail
								.getString("offer_available"));
					}
					if (!jsonObjectDetail.isNull("latitude")) {
						res.setLatitude(String.valueOf(jsonObjectDetail
								.getDouble("latitude")));
					}
					if (!jsonObjectDetail.isNull("longitude")) {
						res.setLongitude(String.valueOf(jsonObjectDetail
								.getDouble("longitude")));
					}

					/*
					 * if (!jsonObjectDetail.isNull("offer_period")) {
					 * res.setOfferPeriod(jsonObjectDetail
					 * .getString("offer_period")); }
					 */

					res.setImage(jsonObjectDetail.getString("image_url"));
					if (jsonObjectDetail.getString("website_link") != null) {
						res.setWebUrl(jsonObjectDetail
								.getString("website_link"));

					}
					if (!jsonObjectDetail.isNull("number_people")
							&& jsonObjectDetail.getString("number_people") != null) {
						res.setPeople(jsonObjectDetail
								.getString("number_people"));
					}
					if (!jsonObjectDetail.isNull("zipcode"))
						res.setZipcode(jsonObjectDetail.getString("zipcode") != null ? jsonObjectDetail
								.getString("zipcode").trim() : "");
					if (!jsonObjectDetail.isNull("num_of_people")
							&& jsonObjectDetail.getString("num_of_people") != null) {
						res.setPeoplePerBook(jsonObjectDetail.getString(
								"num_of_people").trim());
					}

					if (!jsonObjectDetail.isNull("monthly")
							&& !jsonObjectDetail.getString("monthly").trim()
									.equals(""))
						res.setMonthNoOffer(AppUtility
								.parseMonthData(jsonObjectDetail.getString(
										"monthly").trim()));
					if (!jsonObjectDetail.isNull("phone_number")
							&& jsonObjectDetail.getString("phone_number") != null)
						res.setPhoneNo(jsonObjectDetail
								.getString("phone_number"));
					if (!jsonObjectDetail.isNull("booking_number")
							&& jsonObjectDetail.getString("booking_number") != null)
						res.setBookingNo(jsonObjectDetail
								.getString("booking_number"));
					if (!jsonObjectDetail.isNull("cellphone")
							&& jsonObjectDetail.getString("cellphone") != null)
						res.setCellphone((jsonObjectDetail
								.getString("cellphone").toUpperCase(Locale
								.getDefault())));
					if (!jsonObjectDetail.isNull("weekly")
							&& jsonObjectDetail.getJSONArray("weekly") != null) {
						JSONArray jWeekArray = jsonObjectDetail
								.getJSONArray("weekly");
						List<String> listWeeks = new ArrayList<String>();
						for (int j = 0; j < jWeekArray.length(); j++) {
							String element = jWeekArray.getString(j);
							listWeeks.add(AppUtility.parseWeekData(element));
						}
						res.setListWeek(listWeeks);
					}	
					if (!jsonObjectDetail.isNull("offer_period")
							&& jsonObjectDetail.getJSONArray("offer_period") != null) {
						JSONArray jWeekArray = jsonObjectDetail
								.getJSONArray("offer_period");
						if (!jWeekArray.equals("non_available")) {

						}
						List<String> listWeeks = new ArrayList<String>();
						for (int j = 0; j < jWeekArray.length(); j++) {
							String element = jWeekArray.getString(j);
							listWeeks.add(AppUtility.parseWeekData(element));
						}
						res.setOfferPeriod(listWeeks);

					}

					listRestaurants.add(res);
				}

			} else {
				listRestaurants = new ArrayList<Restaurant>();
				String msg = jsonOBject.getString("message");

			}

		} catch (JSONException e) {
			listRestaurants = new ArrayList<Restaurant>();
			Log.e(TAG, e.getMessage());
		}

		return listRestaurants;

	}

	public static List<String> getRestaurantGallery(JSONObject jsonProductobject)
			throws JSONException {
		List<String> imagesList = new ArrayList<String>();
		if (!jsonProductobject.isNull("images")) {
			JSONArray jsonImagesArray = jsonProductobject
					.getJSONArray("images");
			for (int i = 0; i < jsonImagesArray.length(); i++) {
				imagesList.add(jsonImagesArray.getString(i));

			}
		}

		return imagesList;

	}

	public static List<Rating> getRestarantRating(JSONObject jsonProductobject)
			throws JSONException {
		List<Rating> ratingItems = new ArrayList<Rating>();
		if (!jsonProductobject.isNull("allratings")) {
			JSONArray jsonRatingArray = jsonProductobject
					.getJSONArray("allratings");

			for (int i = 0; i < jsonRatingArray.length(); i++) {
				Rating r = new Rating();
				JSONObject jsonObjectRating = jsonRatingArray.getJSONObject(i);

				r.setRatingBy(jsonObjectRating.getString("name"));
				r.setQuotes(jsonObjectRating.getString("review_detail"));
				r.setRatingValue(jsonObjectRating.getInt("rating"));
				r.setRatingDate(jsonObjectRating.getString("date"));
				ratingItems.add(r);
			}
		}

		return ratingItems;

	}

	public static Restaurant getRestaurant(String strJson, Restaurant restaurant) {

		Restaurant restaurantLocal = restaurant;
		JSONObject jsonOBject;
		List<Rating> ratingItems = new ArrayList<Rating>();
		List<String> imagesList = new ArrayList<String>();
		Log.e("resturant detailsss","resturantt detailss");

		try {
			jsonOBject = new JSONObject(strJson);

			if (jsonOBject.getBoolean("success")) {

				JSONObject jsonProductobject = jsonOBject
						.getJSONObject("product_detail");				
				JSONObject jsonObjectDetail = jsonProductobject
						.getJSONObject("detail");
				JSONArray offerArray=jsonObjectDetail.getJSONArray("offer_period");				
				ArrayList<String> offerArraylist=new ArrayList<String>(); 
				
				Log.e("latitiuse",jsonObjectDetail.getString("latitude"));
				Log.e("longitude",jsonObjectDetail.getString("longitude"));
				for(int i=0;i<offerArray.length();i++)
				{
					Log.e("offer value",""+offerArray.get(i));
					offerArraylist.add(""+offerArray.get(i));
				}				
				if (!jsonObjectDetail.isNull("non_available")
						&& jsonObjectDetail.getJSONArray("non_available") != null) {
					JSONArray jWeekArray = jsonObjectDetail
							.getJSONArray("non_available");
					List<String> listWeeks = new ArrayList<String>();
					for (int j = 0; j < jWeekArray.length(); j++) {
						String element = jWeekArray.getString(j);
						listWeeks.add(AppUtility.parseWeekData(element));
					}
					restaurant.setNonavailable(listWeeks);
				}
				ratingItems = getRestarantRating(jsonProductobject);
				imagesList = getRestaurantGallery(jsonProductobject);
				restaurant.setRatings(ratingItems);
				restaurant.setGalleryImages(imagesList);
				restaurant.setOffers(offerArraylist);
				

			} else {
				restaurantLocal.setRatings(ratingItems);
				restaurantLocal.setGalleryImages(imagesList);
				
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}

		return restaurantLocal;

	}

	public static List<Plan> getPlanForNewUsers(String strJson) {

		Plan plan;
		JSONObject jsonOBject;
		List<Plan> listPlans = new ArrayList<Plan>();

		try {
			jsonOBject = new JSONObject(strJson);

			if (jsonOBject.getBoolean("success")) {
				JSONArray jsonPlansArray = null;
				if (!jsonOBject.isNull("products")) {
					jsonPlansArray = jsonOBject.getJSONArray("products");
				}

				if (jsonPlansArray != null) {

					for (int i = 0; i < jsonPlansArray.length(); i++) {

						JSONObject jsonPlan = jsonPlansArray.getJSONObject(i);
						plan = new Plan();
						plan.setPlanName(jsonPlan.getString("name"));
						plan.setPlanId(jsonPlan.getString("id"));
						plan.setPrice(jsonPlan.getString("price"));
						plan.setSubTotalprice(jsonPlan.getString("subtotal"));
						listPlans.add(plan);
					}

				}

			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}

		return listPlans;

	}
}
