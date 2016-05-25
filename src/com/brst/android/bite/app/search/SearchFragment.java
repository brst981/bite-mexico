package com.brst.android.bite.app.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.base.BaseContainerFragment;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.RestaurantDataHandler;
import com.brst.android.bite.app.domain.Restaurant;
import com.brst.android.bite.app.home.RestaurantDetailMain;
import com.brst.android.bite.app.util.JsonParser;
import com.brst.android.bite.app.util.LogMsg;
import com.brst.android.bite.app.util.UI;

public class SearchFragment extends Fragment implements OnItemClickListener {
	private List<Restaurant> lisItems;

	private final String TAG = "SearchFragment";

	EditText textSearch;
	TextView noData;

	ListView listView;

	RestuarantListAdapter adapter;

	ProgressDialog pDialog;
	SharedPreferences mSharedPreferences, mSharedPreferences_;
	RestaurantDataHandler rHandler;
	ImageLoader imageLoader;
	String selectedCity, deafaultCity, deafaultCityID, selectedName,
			selectedPrefCity, selectedPrefID;
	Spinner spinnerCity;
	SpinnerAdapter_ sAdapter;
	ArrayList<String> arrayListID, arrayListName;
	int pos;
	TextView tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageLoader = AppController.getInstance().getImageLoader();
		lisItems = new ArrayList<Restaurant>();
		rHandler = RestaurantDataHandler.getInstance();
		adapter = new RestuarantListAdapter(getActivity(), lisItems);
		mSharedPreferences = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);
		mSharedPreferences_ = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_layout_search, null);
		((TextView) rootView.findViewById(R.id.header_title))
				.setText(R.string.search_header);
		textSearch = (EditText) rootView.findViewById(R.id.search_id);
		noData = (TextView) rootView.findViewById(R.id.noData);
		spinnerCity = (Spinner) rootView.findViewById(R.id.spinner_search);
		// spinnerCity.setOnItemSelectedListener(this);
		arrayListID = new ArrayList<String>();
		arrayListName = new ArrayList<String>();
		deafaultCity = mSharedPreferences.getString(BiteBc.CITY, "");
		deafaultCityID = mSharedPreferences.getString(BiteBc.CITY_ID, "");
		selectedPrefCity = mSharedPreferences_.getString("CITY", "");
		selectedPrefID = mSharedPreferences_.getString("CITY_ID", "");

		addtextSearchListner();

		listView = (ListView) rootView.findViewById(R.id.list_exp_view);
		listView.setOnItemClickListener(this);
		adapter = new RestuarantListAdapter(getActivity(), lisItems);
		listView.setAdapter(adapter);

		requestForCities(Web.CITY);

		arrayListName.add(deafaultCity);
		arrayListID.add(deafaultCityID);
		Log.e("sizee of arraylist",""+arrayListName.size());

		sAdapter = new SpinnerAdapter_(getActivity(),
				R.layout.layout_spinner_item_, arrayListName);

		spinnerCity.setAdapter(sAdapter);

		spinnerCity.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selectedCity = arrayListID.get(position);
				selectedName = arrayListName.get(position);
				/*
				 * if (selectedCity.contains("0000")) {
				 * showAlertDialog(getActivity(), "", "Please select your city",
				 * true);
				 * 
				 * }
				 */

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	/*
	 * class methods including resource initialization and all start here
	 */

	private void addtextSearchListner() {

		textSearch
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							if (validate()) {

								makeRequestForRestaurant(Web.SEARCH, textSearch
										.getText().toString().trim(),
										selectedCity);

							} else {

							}
							return true;
						}
						return false;
					}
				});

		textSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				textSearch.setCompoundDrawables(null, null, null, null);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	// response handler fro service
	protected void parseResturant(String response) {

		UI.hideProgressDialog();

		lisItems = JsonParser.getRestaurantsDetail(response);

		sortList(lisItems);

		if (lisItems != null && lisItems.size() != 0) {
			listView.setVisibility(View.VISIBLE);
			noData.setVisibility(View.GONE);
			adapter.addItems(lisItems);
		} else {
			lisItems = new ArrayList<Restaurant>();
			listView.setVisibility(View.GONE);
			noData.setVisibility(View.VISIBLE);
			LogMsg.LOG(getActivity(), "No se ha encontrado ningún restaurante");
		}

	}

	// sorting list
	private void sortList(List<Restaurant> listItem) {

		Collections.sort(listItem, new Comparator<Restaurant>() {
			@Override
			public int compare(Restaurant r1, Restaurant r2) {
				return r1.getName().compareTo(r2.getName());
			}
		});

	}

	// all dataadabpater for listview and other layout

	class RestuarantListAdapter extends BaseAdapter {

		private Activity activity;
		private LayoutInflater inflater;
		List<Restaurant> lisItems;

		public RestuarantListAdapter(Activity activity,
				List<Restaurant> listsItems) {
			this.activity = activity;
			this.lisItems = listsItems;
		}

		public void addItems(List<Restaurant> listItems) {
			this.lisItems = listItems;
			notifyDataSetChanged();

		}

		@Override
		public int getCount() {

			return lisItems.size();
		}

		@Override
		public Object getItem(int position) {

			return lisItems.get(position);
		}

		@Override
		public long getItemId(int id) {

			return id;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			final ViewHolder holder;

			if (inflater == null)
				inflater = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.layout_list_restaurant_item, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView
						.findViewById(R.id.text_r_name);
				holder.cusine = (TextView) convertView
						.findViewById(R.id.text_r_c_type);

				holder.image = (ImageView) convertView
						.findViewById(R.id.image_id);
				// holder.iconAdd.setImageResource(R.drawable.btn_plus);
				holder.offer = (TextView) convertView
						.findViewById(R.id.discount);
				holder.rate = (RatingBar) convertView
						.findViewById(R.id.rating_rest);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Restaurant item = lisItems.get(position);
			holder.name.setText(item.getName());
			holder.cusine.setText(item.getCuisine());

			// holder.offer
			// .setText(!item.getOfferAvailable().equals("No Offers") ? item
			// .getOfferAvailable().equals("31") ? "2 for 1 meal"
			// : "50% off food" : item.getOfferAvailable());
			holder.rate.setProgress(item.getRating());

			if (item.getOfferAvailable().equals("31")) {
				holder.offer.setText("2 por 1");

			} else if (item.getOfferAvailable().equals("32")) {
				holder.offer.setText("50% de descuento en alimentos");

			} else if (item.getOfferAvailable().equals("100")) {
				holder.offer.setText("2 por 1");

			} else if (item.getOfferAvailable().equals("101")) {
				holder.offer.setText("50% de descuento en alimentos");

			} else if (item.getOfferAvailable().equals("102")) {
				holder.offer.setText("40% de descuento en alimentos");

			} else if (item.getOfferAvailable().equals("103")) {
				holder.offer.setText("40% de descuento en alimentos");

			}

			else if (item.getOfferAvailable().equals("104")) {
				holder.offer.setText("40% de descuento en alimentos");

			}

			else if (item.getOfferAvailable().equals("105")) {
				holder.offer.setText("30% de descuento en alimentos");

			}

			else if (!item.getOfferAvailable().equals("No Offers")) {
				holder.offer.setText("No Offers");

			}

			// If you are using normal ImageView
			imageLoader.get(item.getImage(), new ImageListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Log.i(TAG, "Image Load Error: " + error.getMessage());
				}

				@Override
				public void onResponse(ImageContainer response, boolean arg1) {
					if (response.getBitmap() != null) {
						// load image into imageview

						holder.image.setImageBitmap(response.getBitmap());

					}
				}
			});

			return convertView;
		}
	}

	static class ViewHolder {
		TextView name;
		TextView cusine;
		ImageView image;
		TextView offer;
		RatingBar rate;
	}

	/**
	 * All services call using volley * @param product
	 * 
	 * @param searchString
	 * @param selectedCity
	 */
	private void makeRequestForRestaurant(String product,
			final String searchString, final String selectedCity) {

		String url = Web.HOST + product;

		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e(TAG + "On Response", response);

						parseResturant(response);
						hideKeyboard();
					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e(TAG, "Error: " + error.getMessage());
						UI.hideProgressDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put(DataKey.DATA_SEARCH, searchString);

				params.put(DataKey.DATA_CITY, selectedCity);

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				DataKey.TAG_JSON_OBJECT);

	}

	/**
	 * Not working no need to fetch all restaurant on activity start
	 * 
	 * @param response
	 */

	// private void makeRequestForRestaurant(String product) {
	//
	// String url = Web.HOST + product;
	// showProgressDialog();
	// StringRequest strReq = new StringRequest(Method.GET, url,
	// new com.android.volley.Response.Listener<String>() {
	//
	// @Override
	// public void onResponse(String response) {
	// Log.e(TAG + "On Response", response);
	// parseResturant(response);
	// }
	// }, new com.android.volley.Response.ErrorListener() {
	//
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// VolleyLog.e(TAG, "Error: " + error.getMessage());
	// hideProgressDialog();
	// }
	// });
	//
	// // Adding request to request queue
	// AppController.getInstance().addToRequestQueue(strReq,
	// DataKey.TAG_JSON_OBJECT);
	//
	// }

	// all click listener starts here

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Restaurant item = lisItems.get(position);

		rHandler.setRestaurant(item);
		// String product_id = item.getId();

		RestaurantDetailMain fragment = new RestaurantDetailMain();
		// Bundle savedInstanceState = new Bundle();
		// savedInstanceState.putString("ENTITY_ID", product_id);
		// fragment.setArguments(savedInstanceState);
		((BaseContainerFragment) getParentFragment()).replaceFragment(fragment,
				true);

	}

	private void requestForCities(String city) {
		// TODO Auto-generated method stub
		String url = Web.HOST + city;

		UI.showProgressDialog(getActivity());

		StringRequest strReq = new StringRequest(Method.GET, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("responsee of city",""+response);

						getCities(response);
						UI.hideProgressDialog();

					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error: " + error.getMessage());
						// arrayListName.add("0000");
					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				WSConstant.DataKey.TAG_JSON_OBJECT);
	}

	protected void getCities(String response) {
		// TODO Auto-generated method stub

		try {
			arrayListName.clear();
			arrayListID.clear();
			arrayListID.add("0000");

			arrayListName.add("Ciudad selecta");

			JSONArray array = new JSONArray(response);

			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				String id = jsonObject.getString("id");
				String name = jsonObject.getString("name");

				arrayListID.add(id);

				arrayListName.add(name);

			}

			 sAdapter.notifyDataSetChanged();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
		}

	}

	private class SpinnerAdapter_ extends ArrayAdapter<String> {
		Context context;
		// String[] items = new String[] {};
		ArrayList<String> arrayList;

		public SpinnerAdapter_(final Context context,
				final int textViewResourceId, ArrayList<String> arrayList) {
			super(context, textViewResourceId, arrayList);
			this.arrayList = arrayListName;
			this.context = context;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(
						R.layout.layout_spinner_dropdown, parent, false);
			}

			tv = (TextView) convertView.findViewById(R.id.txt_d_id);
			tv.setText(arrayList.get(position));
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(15);
			return convertView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.layout_spinner_item_,
						parent, false);
			}

			tv = (TextView) convertView.findViewById(R.id.txt_id);
			tv.setText(arrayList.get(position));
			tv.setTextColor(Color.BLACK);
			// tv.setHint("City");

			tv.setTextSize(15);

			if (position == 0) {
				tv.setTextColor(Color.BLACK);
			}
			return convertView;
		}

	}

	public void showAlertDialog(Context context, String title, String message,
			boolean error) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();

					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	private boolean validate() {

		if (textSearch.getText().toString().trim().equals("")) {
			showAlertDialog(getActivity(), "Error", "Please enter text", true);
			return false;
		}
		if (selectedCity.contains("0000")) {
			showAlertDialog(getActivity(), "Error", "Please select your city",
					true);
			return false;
		}

		return true;
	}

	private void hideKeyboard() {
		// TODO Auto-generated method stub
		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
