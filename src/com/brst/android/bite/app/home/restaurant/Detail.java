package com.brst.android.bite.app.home.restaurant;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.RestaurantDataHandler;
import com.brst.android.bite.app.domain.Restaurant;

@SuppressLint("DefaultLocale")
public class Detail extends Fragment implements OnClickListener {

	private static final String TAG = "RestaurantDetail.Detail";

	String restDetails;

	ImageView imageHotel;
	TextView discount, restrictday, webUrl, restrictedMonth, restrictPeople,
			phoneNumber, textCall, txtRestauranrInfo, txtCuisine, txtOffertype,
			txtNoOfPeople, txtRestauranrInfoDetails, txtCuisineDetails,
			txtOffertypeDetails, txtNoOfPeopleDetails;

	RelativeLayout relativeLayoutDiscount, relativeLayoutTextPeople,
			relativeLayoutMonth, relativeLayoutCall;

	LinearLayout relativeLayoutWeekOne, relativeLayoutWeekTwo,
			relativeLayoutWeekThree, relativeLayoutWeekFour,
			relativeLayoutWeekFive, relativeLayoutWeekSix,
			relativeLayoutWeekSeventh;
	TextView ratingNum;
	RatingBar rate;

	RestaurantDataHandler rHandler;
	Restaurant restaurant;
	ImageLoader imageLoader;
	ImageView imageWeb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageLoader = AppController.getInstance().getImageLoader();
		rHandler = RestaurantDataHandler.getInstance();
		restaurant = rHandler.getRestaurant();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_layout_detail, null);
		Log.e("in detail page", "in detail");

		imageHotel = (ImageView) rootView.findViewById(R.id.image_hotel_2);
		relativeLayoutDiscount = (RelativeLayout) rootView
				.findViewById(R.id.layout_text_r_discount);

		txtRestauranrInfo = (TextView) rootView
				.findViewById(R.id.text_group_info);
		txtCuisine = (TextView) rootView.findViewById(R.id.text_group_cuisine);
		txtOffertype = (TextView) rootView
				.findViewById(R.id.text_group_offerType);
		txtNoOfPeople = (TextView) rootView
				.findViewById(R.id.text_group_no_people);
		txtRestauranrInfoDetails = (TextView) rootView
				.findViewById(R.id.text_child_info_detail);
		txtCuisineDetails = (TextView) rootView
				.findViewById(R.id.text_child_cuisine_detail);
		txtOffertypeDetails = (TextView) rootView
				.findViewById(R.id.text_child_offerType_detail);
		txtNoOfPeopleDetails = (TextView) rootView
				.findViewById(R.id.text_child_no_people_detail);

		discount = (TextView) rootView.findViewById(R.id.text_r_discount);
		// restrictday = (TextView) rootView.findViewById(R.id.text_day);
		// relativeLayoutWeekOffer = (RelativeLayout) rootView
		// .findViewById(R.id.layout_text_day);
		restrictedMonth = (TextView) rootView.findViewById(R.id.text_month);
		relativeLayoutMonth = (RelativeLayout) rootView
				.findViewById(R.id.layout_text_Month);
		restrictPeople = (TextView) rootView.findViewById(R.id.text_people);
		relativeLayoutTextPeople = (RelativeLayout) rootView
				.findViewById(R.id.layout_text_people);
		relativeLayoutCall = (RelativeLayout) rootView
				.findViewById(R.id.layout_text_call);
		relativeLayoutWeekOne = (LinearLayout) rootView
				.findViewById(R.id.layout_week_1);
		relativeLayoutWeekTwo = (LinearLayout) rootView
				.findViewById(R.id.layout_week_2);
		relativeLayoutWeekThree = (LinearLayout) rootView
				.findViewById(R.id.layout_week_3);
		relativeLayoutWeekFour = (LinearLayout) rootView
				.findViewById(R.id.layout_week_4);
		relativeLayoutWeekFive = (LinearLayout) rootView
				.findViewById(R.id.layout_week_5);
		relativeLayoutWeekSix = (LinearLayout) rootView
				.findViewById(R.id.layout_week_6);

		textCall = (TextView) rootView.findViewById(R.id.text_call);

		webUrl = (TextView) rootView.findViewById(R.id.text_web_url);
		webUrl.setOnClickListener(this);

		ratingNum = (TextView) rootView.findViewById(R.id.rating_num);
		rate = (RatingBar) rootView.findViewById(R.id.rating_bar);

		phoneNumber = (TextView) rootView.findViewById(R.id.text_phone_num);
		imageWeb=(ImageView)rootView.findViewById(R.id.image_web);
		phoneNumber.setOnClickListener(this);

		loadData();

		// Set the Items of Parent
		setGroupParents();
		// Set The Child Data
		setChildData();

		// Utility.setListViewHeightBasedOnChildren(expandableListView);
		return rootView;
	}

	@SuppressLint("DefaultLocale")
	private void loadData() {
		// TODO Auto-generated method stub
		imageLoader.get(restaurant.getImage(), new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Image Load Error: " + error.getMessage());
			}

			@Override
			public void onResponse(ImageContainer response, boolean arg1) {
				if (response.getBitmap() != null) {
					// load image into imageview
					imageHotel.setImageBitmap(response.getBitmap());
				}
			}
		});

		if (restaurant.getOfferAvailable().equals("31")) {
			discount.setText("2 por 1 comidas");
		} else if (restaurant.getOfferAvailable().equals("32")) {
			discount.setText("50% de descuento en alimentos");

		} else if (restaurant.getOfferAvailable().equals("100")) {
			discount.setText("2 por 1 comidas");

		} else if (restaurant.getOfferAvailable().equals("101")) {
			discount.setText("50% de descuento en alimentos");

		}

		else if (restaurant.getOfferAvailable().equals("102")) {
			discount.setText("40% de descuento en alimentos");

		} else if (restaurant.getOfferAvailable().equals("103")) {
			discount.setText("30% de descuento en alimentos");

		}

		else if (restaurant.getOfferAvailable().equals("104")) {
			discount.setText("40% de descuento en alimentos");

		}

		else if (restaurant.getOfferAvailable().equals("105")) {
			discount.setText("30% de descuento en alimentos");

		}

		else if (!restaurant.getOfferAvailable().equals("No Offers")) {
			discount.setText("No hay ofertas");

		}

		if (!restaurant.getMonthNoOffer().equals("")) {
			restrictedMonth.setText(restaurant.getMonthNoOffer());
		} else {
			relativeLayoutMonth.setVisibility(View.GONE);
		}
		if (!restaurant.getPeoplePerBook().equals("0")) {

			restrictPeople.setText("x" + restaurant.getPeoplePerBook());

		} else {
			relativeLayoutTextPeople.setVisibility(View.GONE);
		}

		if (restaurant.getCellphone().equals("YES")) {
			relativeLayoutCall.setVisibility(View.VISIBLE);
		} else {
			relativeLayoutCall.setVisibility(View.GONE);
		}

		if (restaurant.getPhoneNo() != null) {
			phoneNumber.setText(restaurant.getPhoneNo());
		} else {
			phoneNumber.setVisibility(View.GONE);
		}
		Log.e("offer perio", "" + restaurant.getOfferPeriod());
		// if (restaurant.getOfferPeriod()!= null

		if (restaurant.getOffers() != null
				&& restaurant.getOffers().size() != 0) {

			List<String> weekElement = restaurant.getOffers();
			if (weekElement.size() > 6) {
				relativeLayoutWeekThree.setVisibility(View.VISIBLE);
				relativeLayoutWeekTwo.setVisibility(View.VISIBLE);

			} else if (weekElement.size() >= 4) {
				relativeLayoutWeekTwo.setVisibility(View.VISIBLE);

			}

			for (int i = 0; i < weekElement.size(); i++) {
				String element = weekElement.get(i);
				if (element.contains("No-restriction")) {
					break;
				}
				relativeLayoutWeekOne.setVisibility(View.VISIBLE);
				View view = inflateView_(element);
				if (i >= 0 && i <= 2) {
					relativeLayoutWeekOne.addView(view);
				} else if (i >= 3 && i <= 5) {
					relativeLayoutWeekTwo.addView(view);
				} else if (i == 6) {
					relativeLayoutWeekThree.addView(view);
				}

			}
		}
		Log.e("non available", "" + restaurant.getNonavailable());
		if ((restaurant.getNonavailable() != null && restaurant
				.getNonavailable().size() != 0)
				) {
			if(!restaurant.getNonavailable().toString().equals("[NO]"))
			{

			List<String> weekElement = restaurant.getNonavailable();
			Log.e("week element", "" + weekElement);
			if (weekElement.size() > 6) {
				relativeLayoutWeekSix.setVisibility(View.VISIBLE);
				relativeLayoutWeekFive.setVisibility(View.VISIBLE);
			} else if (weekElement.size() > 4) {
				relativeLayoutWeekFive.setVisibility(View.VISIBLE);
			}
			for (int i = 0; i < weekElement.size(); i++) {
				String element = weekElement.get(i);
				if (element.contains("No-restriction")) {
					break;
				}
				relativeLayoutWeekFour.setVisibility(View.VISIBLE);
				View view = inflateView(element);
				if (i >= 0 && i <= 2) {
					relativeLayoutWeekFour.addView(view);
				} else if (i >= 3 && i <= 5) {
					relativeLayoutWeekFive.addView(view);

				} else if (i == 6) {
					relativeLayoutWeekSix.addView(view);
				}

			}
		}

		webUrl.setText(restaurant.getWebUrl());
		Log.e("urll data","datass"+restaurant.getWebUrl());
		if(restaurant.getWebUrl().equals("http://"))
		{
			webUrl.setVisibility(View.INVISIBLE);
			imageWeb.setVisibility(View.INVISIBLE);
		}

		rate.setProgress(restaurant.getRating());
		ratingNum.setText(String.valueOf((double) restaurant.getRating()));
		}
	}

	private View inflateView(String element) {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.layout_week_info_item, null);

		TextView dayNoOffer = (TextView) view.findViewById(R.id.text_day);

		dayNoOffer.setText(element);

		return view;
	}

	private View inflateView_(String element) {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.layout_week_info_item_, null);

		TextView dayNoOffer = (TextView) view.findViewById(R.id.text_day);

		dayNoOffer.setText(element);

		return view;
	}

	private void setChildData() {
		// Add Child Items for Fruits

		// Add Child Items for Birds

		restDetails = restaurant.getDescription();
		if (restDetails != null) {
			txtRestauranrInfoDetails.setText(restDetails);
		}

		if (restaurant.getCuisine() != null) {
			txtCuisineDetails.setText(restaurant.getCuisine());
		}

		// Add Child Items for Flowers

		if (restaurant.getOfferAvailable().equals("31")) {
			txtOffertypeDetails.setText("2 por 1 comidas");

		} else if (restaurant.getOfferAvailable().equals("32")) {
			txtOffertypeDetails.setText("50% de descuento en alimentos");

		} else if (restaurant.getOfferAvailable().equals("100")) {
			txtOffertypeDetails.setText("2 for 1");

		} else if (restaurant.getOfferAvailable().equals("101")) {
			txtOffertypeDetails.setText("50% de descuento en alimentos");

		}

		else if (restaurant.getOfferAvailable().equals("102")) {
			txtOffertypeDetails.setText("40% de descuento en alimentos");

		} else if (restaurant.getOfferAvailable().equals("103")) {
			txtOffertypeDetails.setText("30% de descuento en alimentos");

		}

		else if (restaurant.getOfferAvailable().equals("104")) {
			txtOffertypeDetails.setText("40% de descuento en alimentos");

		}

		else if (restaurant.getOfferAvailable().equals("105")) {
			txtOffertypeDetails.setText("30% de descuento en alimentos");

		}

		else if (!restaurant.getOfferAvailable().equals("No Offers")) {
			txtOffertypeDetails.setText("Sin ofertas");

		} // child.add(!restaurant.getOfferAvailable().equals("No Offers") ?
			// restaurant // .getOfferAvailable().equals("31") ? "2 for 1 meal"
			// : "50% off food" : restaurant.getOfferAvailable());

		// Add Child Items for Animals child = new ArrayList<String>();

		// child.add(restaurant.getPeoplePerBook());

		if (restaurant.getPeople() != null) {
			txtNoOfPeopleDetails.setText(restaurant.getPeople());
		}

	}

	private void setGroupParents() {

		txtRestauranrInfo.setText("INFORMACION DEL RESTAURANT");
		txtCuisine.setText("TIPO DE COMIDA");
		txtOffertype.setText("TIPO DE OFERTA");
		txtNoOfPeople.setText("NUMERO DE PERSONAS");

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.text_web_url:

			String url = webUrl.getText().toString();

			if (!url.startsWith("http://") && !url.startsWith("https://"))
				url = "http://" + url;
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(url));
			startActivity(browserIntent);

			break;
		case R.id.text_phone_num:

			String uri2 = "tel:" + phoneNumber.getText().toString().trim();
			Intent intent2 = new Intent(Intent.ACTION_CALL);
			intent2.setData(Uri.parse(uri2));
			startActivity(intent2);
			break;

		default:
			break;
		}

	}

}
