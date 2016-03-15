package com.brst.android.bite.app.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bite.app.base.BaseContainerFragment;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.RestaurantDataHandler;
import com.brst.android.bite.app.domain.Restaurant;
import com.brst.android.bite.app.util.JsonParser;
import com.brst.android.bite.app.util.UI;
import com.brst.android.bitemaxico.app.R;

/**
 * @author ShalviSharma
 * 
 *         ${tags}
 */
public class Home extends Fragment implements OnClickListener,
		OnItemClickListener {

	public static final int FRAGMENT_CODE = 328;
	private RestaurantDataHandler rHandler;

	List<Restaurant> listItems;
	private static final String TAG = "Home";

	ListView listRestaurant;
	RestuarantListAdapter adapter;
	Button btnFilter;
	ImageView refresh;

	// These tags will be used to cancel the requests
	private String tag_json_obj = "jobj_req";
	SharedPreferences sharedpreferences;
	private String cVersionName, uVersionName;
	Editor editor;
	String mID;
	ArrayList<String> arrayListID, arrayListName;
	SpinnerAdapter_ sAdapter;
	String selected, customerID;
	ImageLoader imageLoader;
	TextView txtHeader;
	String customer_id, test;
	String mCity, City, id_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageLoader = AppController.getInstance().getImageLoader();
		rHandler = RestaurantDataHandler.getInstance();
		sharedpreferences = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);
		editor=sharedpreferences.edit();
		customer_id = sharedpreferences.getString(BiteBc.CUST_ID, "");
		mCity = sharedpreferences.getString(BiteBc.CITY, "");
		
		Log.e("user id final",sharedpreferences.getString(BiteBc.USER_ID_FINAL, ""));
		Log.e("user password final",sharedpreferences.getString(BiteBc.USER_PWD_FINAL, ""));


		arrayListID = new ArrayList<String>();
		arrayListName = new ArrayList<String>();
		mCity="";
		Log.e("city status","data"+sharedpreferences.getString("cityData", ""));
		if (mCity != null) {
			if (mCity.equals("")&&sharedpreferences.getString("cityData", "").equals("")) {

				requestForCities(Web.CITY);

				final Dialog dialog = new Dialog(getActivity());
				dialog.setCancelable(false);
				dialog.setTitle("Selecciona Tu ciudad.");
				editor.putString("cityData", "data");
				editor.commit();
				dialog.setContentView(R.layout.custom_dialog_box);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setCancelable(false);

				Spinner spinner = (Spinner) dialog
						.findViewById(R.id.spinner_city_dialog);

				sAdapter = new SpinnerAdapter_(getActivity(),
						R.layout.layout_spinner_item_, arrayListName);

				spinner.setAdapter(sAdapter);
				spinner.setSelection(1);

				spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						id_ = arrayListID.get(position);
						selected = parent.getItemAtPosition(position)
								.toString();

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});

				Button buttonSubmit = (Button) dialog
						.findViewById(R.id.buttonSUbmit);
				buttonSubmit.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						if (selected.contains("Ciudad selecta")) {
							showAlert_Dialog(getActivity(), "",
									"Por favor seleccione su ciudad", true);
						} else {
							postData();
							dialog.dismiss();
						}
					}
				});

				dialog.show();
			}

			else {

			}

		} else {

			requestForCities(Web.CITY);

			final Dialog dialog = new Dialog(getActivity());
			dialog.setTitle("Selecciona Tu ciudad.");

			dialog.setContentView(R.layout.custom_dialog_box);
			dialog.setCanceledOnTouchOutside(false);
			Spinner spinner = (Spinner) dialog
					.findViewById(R.id.spinner_city_dialog);

			arrayListName.add("Seleccionar Ciudad");
			sAdapter = new SpinnerAdapter_(getActivity(),
					R.layout.layout_spinner_item_, arrayListName);

			spinner.setAdapter(sAdapter);
			spinner.setSelection(1);

			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					selected = parent.getItemAtPosition(position).toString();

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});

			Button buttonSubmit = (Button) dialog
					.findViewById(R.id.buttonSUbmit);
			buttonSubmit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					postData();
					dialog.dismiss();
				}
			});

			dialog.show();
		}

		listItems = new ArrayList<Restaurant>();

		try {
			cVersionName = getActivity().getPackageManager().getPackageInfo(
					getActivity().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		makeRequestForUpgrade(Web.UPGRADE_ANDROID);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home_rest_listing,
				container, false);
		txtHeader = (TextView) rootView.findViewById(R.id.header_title);

		City = sharedpreferences.getString(BiteBc.CITY, "");

		if (City != null) {
			txtHeader.setText(City);

		} else {
			txtHeader.setText(R.string.step_1_title);
		}

		listRestaurant = (ListView) rootView.findViewById(R.id.list_view);

		btnFilter = (Button) rootView.findViewById(R.id.btn_filter);
		btnFilter.setOnClickListener(this);

		refresh = (ImageView) rootView.findViewById(R.id.refresh);
		refresh.setOnClickListener(this);
		// intiProgressDailog();
		// setupSearchView();
		adapter = new RestuarantListAdapter(getActivity(), listItems);
		listRestaurant.setAdapter(adapter);
		listRestaurant.setOnItemClickListener(this);
		postID();
		return rootView;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	/*
	 * public void onActivityResult(int requestCode, int resultCode, Intent
	 * data) { if (requestCode == FRAGMENT_CODE && resultCode ==
	 * Activity.RESULT_OK) { if (data != null) {
	 * 
	 * String value = data.getStringExtra("people"); String cusine =
	 * data.getStringExtra("cusine"); String offer1 =
	 * data.getStringExtra("offer1"); String availbilty =
	 * data.getStringExtra("availbity"); // String offer2 =
	 * data.getStringExtra("offer2");
	 * 
	 * 
	 * if (value != null || cusine != null || offer1 != null) { Log.e(TAG,
	 * "Data passed from Child fragment = " + value + cusine);
	 * 
	 * HashMap<String, String> param = new HashMap<String, String>();
	 * param.put("cuisine_types[]", cusine); param.put("people[]", value);
	 * param.put("offer_type[]", offer1); param.put("availability[]",
	 * availbilty); param.put("customerid", customer_id);
	 * 
	 * makeRequestForFilter(Web.FILTER, param);
	 * 
	 * } else { Toast.makeText(getActivity(), "Result not found",
	 * Toast.LENGTH_LONG).show(); } } } }
	 */

	class RestuarantListAdapter extends BaseAdapter {

		private Activity activity;
		private LayoutInflater inflater;
		List<Restaurant> listItems;

		public RestuarantListAdapter(Activity activity,
				List<Restaurant> listsItems) {
			this.activity = activity;
			this.listItems = listsItems;
		}

		public void addItems(List<Restaurant> listItems) {
			this.listItems = listItems;
			notifyDataSetChanged();

		}

		@Override
		public int getCount() {

			return listItems.size();
		}

		@Override
		public Object getItem(int position) {

			return listItems.get(position);
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

			final Restaurant item = listItems.get(position);
			holder.name.setText(item.getName());
			holder.cusine.setText(item.getCuisine());

			if (item.getOfferAvailable().equals("31")) {
				holder.offer.setText("2 por 1 ");

			} else if (item.getOfferAvailable().equals("32")) {
				holder.offer.setText("50% de descuento en alimentos");

			} else if (item.getOfferAvailable().equals("100")) {
				holder.offer.setText("2 por 1");

			} else if (item.getOfferAvailable().equals("101")) {
				holder.offer.setText("50% off");

			} else if (item.getOfferAvailable().equals("102")) {
				holder.offer.setText("40% off ");

			} else if (item.getOfferAvailable().equals("103")) {
				holder.offer.setText("30% off");

			}

			else if (item.getOfferAvailable().equals("104")) {
				holder.offer.setText("40% off food bill");

			}

			else if (item.getOfferAvailable().equals("105")) {
				holder.offer.setText("30% off food bill");

			}

			else if (!item.getOfferAvailable().equals("No Offers")) {
				holder.offer.setText("No Offers");

			}

			// holder.offer
			// .setText(!item.getOfferAvailable().equals("No Offers") ? item
			// .getOfferAvailable().equals("31") ? "2 for 1 meal"
			// : "50% off food" : item.getOfferAvailable());
			holder.rate.setProgress(item.getRating());

			// If you are using normal ImageView

		

			imageLoader.get(item.getImage(), new ImageListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Log.e(TAG, "Image Load Error: " + error.getMessage());
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

	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.btn_filter:
			Filter fragment = new Filter();
			fragment.setTargetFragment(this, FRAGMENT_CODE);
			((BaseContainerFragment) getParentFragment()).replaceFragment(
					fragment, true);
			break;
		case R.id.refresh:
			listItems = new ArrayList<Restaurant>();
			postID();

			break;

		default:
			break;
		}

	}

	private void postID() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("customerid", customer_id);
		makeRequestForRestaurant(Web.PRODUCT, params);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Restaurant item = listItems.get(position);

		rHandler.setRestaurant(item);

		RestaurantDetailMain fragment = new RestaurantDetailMain();

		((BaseContainerFragment) getParentFragment()).replaceFragment(fragment,
				true);

	}

	private void makeRequestForRestaurant(String product,
			final Map<String, String> rParams) {

		String url = Web.HOST + product;

		UI.showProgressDialog(getActivity());

		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("Home response", response);
						// msgResponse.setText(response.toString());
						parseResturant(response);

					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d("", "Error: " + error.getMessage());
						UI.hideProgressDialog();
					}
				}) {

			/**
			 * Passing some request headers
			 * */
			// @Override
			// public Map<String, String> getHeaders() throws AuthFailureError {
			// HashMap<String, String> headers = new HashMap<String, String>();
			// headers.put("Content-Type", "application/json");
			// return headers;
			// }

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = rParams;
				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

	}

	private void makeRequestForUpgrade(String upgradeAndroid) {
		// TODO Auto-generated method stub
		String url = Web.HOST + upgradeAndroid;
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// msgResponse.setText(response.toString());
						parseResponseForUpgrade(response);
					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e(TAG, "Error: " + error.getMessage());
						UI.hideProgressDialog();
					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

	}

	protected void parseResponseForUpgrade(String response) {

		JSONObject jsonOBject;
		try {
			jsonOBject = new JSONObject(response);
			if (jsonOBject.getBoolean("success")) {

				// uVersionName = jsonOBject.getString("version");
				// String message = jsonOBject.getString("message");
				// final String link = jsonOBject.getString("link");
				//
				// double cVersion = Double.parseDouble(cVersionName);
				// double uVersion = Double.parseDouble(uVersionName);
				// if (uVersion > cVersion) {
				// Log.e("UpgradeNeeded", "Upgrade needed");
				//
				// new AlertDialog.Builder(getActivity())
				// .setIcon(R.drawable.ic_launcher)
				// .setTitle("Update available")
				// .setCancelable(false)
				// .setMessage(message)
				// .setNegativeButton("Update now",
				// new DialogInterface.OnClickListener() {
				// public void onClick(
				// DialogInterface dialog,
				// int whichButton) {
				// Intent intent = new Intent(
				// Intent.ACTION_VIEW, Uri
				// .parse(link));
				// startActivity(intent);
				// getActivity().finish();
				// }
				// })
				// .setPositiveButton("Later",
				// new DialogInterface.OnClickListener() {
				// public void onClick(
				// DialogInterface dialog,
				// int whichButton) {
				// /* User clicked Cancel */
				// dialog.dismiss();
				// }
				// }).show();
				// }

			}

		} catch (JSONException e) {

		}
	}

	protected void parseResturant(String response) {

		listItems = JsonParser.getRestaurantsDetail(response);

		if (listItems != null && listItems.size() != 0) {
			adapter.addItems(listItems);
		} else {
			// LogMsg.LOG(getActivity(), "No search Available");
			showAlert_Dialog(getActivity(), "Estado", "Próximamente", true);
			adapter.addItems(listItems);

		}
		UI.hideProgressDialog();

	}

	/*
	 * protected void parseResturant_(String response) { listItems =
	 * JsonParser.getRestaurantsDetail(response);
	 * 
	 * if (listItems != null && listItems.size() != 0) {
	 * adapter.addItems(listItems); } else { // LogMsg.LOG(getActivity(),
	 * "No search Available"); showAlert_Dialog(getActivity(), "Status",
	 * "No records found", true); adapter.addItems(listItems);
	 * 
	 * 
	 * } UI.hideProgressDialog();
	 * 
	 * }
	 */

	/*
	 * private void makeRequestForFilter(String register, final HashMap<String,
	 * String> param) {
	 * 
	 * String url = Web.HOST + register;
	 * 
	 * 
	 * UI.showProgressDialog(getActivity()); StringRequest strReq = new
	 * StringRequest(Method.POST, url, new
	 * com.android.volley.Response.Listener<String>() {
	 * 
	 * @Override public void onResponse(String response) { Log.e(TAG +
	 * "----On Response", response); //
	 * msgResponse.setText(response.toString());
	 * 
	 * parseResturant(response);
	 * 
	 * } }, new com.android.volley.Response.ErrorListener() {
	 * 
	 * @Override public void onErrorResponse(VolleyError error) {
	 * VolleyLog.e(TAG, "Error: " + error.getMessage());
	 * UI.hideProgressDialog(); } }) {
	 *//**
	 * Passing some request headers
	 * */
	/*
	 * // @Override // public Map<String, String> getHeaders() throws
	 * AuthFailureError { // HashMap<String, String> headers = new
	 * HashMap<String, String>(); // headers.put("Content-Type",
	 * "application/json"); // return headers; // }
	 * 
	 * @Override protected Map<String, String> getParams() { Map<String, String>
	 * params = param; return params; }
	 * 
	 * };
	 * 
	 * // Adding request to request queue
	 * AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
	 * 
	 * // Cancelling request //
	 * ApplicationController.getInstance().getRequestQueue
	 * ().cancelAll(tag_json_obj);
	 * 
	 * }
	 */

	private void requestForCities(String city) {
		// TODO Auto-generated method stub
		String url = Web.HOST + city;

		UI.showProgressDialog(getActivity());

		StringRequest strReq = new StringRequest(Method.GET, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						getCities(response);
						UI.hideProgressDialog();

					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error: " + error.getMessage());

					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				WSConstant.DataKey.TAG_JSON_OBJECT);
	}

	protected void getCities(String response) {
		// TODO Auto-generated method stub

		try {
			arrayListName.add("Ciudad selecta");
			arrayListID.add("0");

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

			TextView tv = (TextView) convertView.findViewById(R.id.txt_d_id);
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

			TextView tv = (TextView) convertView.findViewById(R.id.txt_id);
			tv.setText(arrayList.get(position));
			tv.setTextColor(Color.BLACK);
			// tv.setHint("City");

			tv.setTextSize(15);

			if (position == 0) {
				tv.setTextColor(Color.GRAY);
			}
			return convertView;
		}

	}

	protected void postData() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("customerid", customer_id);
		params.put("city", selected);
		Editor editor = sharedpreferences.edit();
		editor.putString(BiteBc.CITY, selected);

		editor.putString(BiteBc.CITY_ID, id_);
		editor.commit();
		if (selected == null) {
			txtHeader.setText(R.string.step_1_title);
		} else {
			txtHeader.setText(selected);
		}

		updateCustomerCity(Web.UPDATE_CITY, params);

	}

	private void updateCustomerCity(String updateCity,
			final Map<String, String> rParams) {
		// TODO Auto-generated method stub

		UI.showProgressDialog(getActivity());
		String url = Web.HOST + updateCity;

		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						// msgResponse.setText(response.toString());
						status(response);
						UI.hideProgressDialog();

					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d("", "Error: " + error.getMessage());
						UI.hideProgressDialog();
					}
				}) {

			/**
			 * Passing some request headers
			 * */
			// @Override
			// public Map<String, String> getHeaders() throws AuthFailureError {
			// HashMap<String, String> headers = new HashMap<String, String>();
			// headers.put("Content-Type", "application/json");
			// return headers;
			// }

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = rParams;
				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				WSConstant.DataKey.TAG_JSON_OBJECT);

	}

	protected void status(String response) {
		// TODO Auto-generated method stub
        Log.e("response",response);
		try {
			JSONObject jsonOBject = new JSONObject(response);
			if (jsonOBject.getBoolean("success")) {

				String msg = jsonOBject.getString("message");
				showAlertDialog(getActivity(), "Estado", msg, true);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void showAlert_Dialog(Context context, String title, String message,
			boolean error) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
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

	public void showAlertDialog(Context context, String title, String message,
			boolean error) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity

						dialog.cancel();
						postID();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

}
