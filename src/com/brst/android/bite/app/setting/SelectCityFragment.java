package com.brst.android.bite.app.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.User;
import com.brst.android.bite.app.util.UI;

public class SelectCityFragment extends Fragment implements
		OnItemSelectedListener {
	Spinner spinnerCity;
	SpinnerAdapter_ sAdapter;
	ArrayList<String> arrayListID, arrayListName;
	String selectedCity, deafaultCity, mID;
	UserDataHandler uDataHandler;
	User user;
	Button buttonSubmit;
	SharedPreferences mSharedPreferences;
	String id, name, cityID_, cityId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uDataHandler = UserDataHandler.getInstance();
		user = uDataHandler.getUser();
		mSharedPreferences = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);
		deafaultCity = mSharedPreferences.getString(BiteBc.CITY, "");
		cityID_ = mSharedPreferences.getString(BiteBc.CITY_ID, "");

		mID = mSharedPreferences.getString(BiteBc.CUST_ID, "");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View rootView = inflater.inflate(R.layout.fragment_setting_city,
				container, false);
		((TextView) rootView.findViewById(R.id.header_title))
				.setText(R.string.setting_city);

		spinnerCity = (Spinner) rootView.findViewById(R.id.spinner_user);

		buttonSubmit = (Button) rootView.findViewById(R.id.btnSUbmit_City);
		deafaultCity = mSharedPreferences.getString(BiteBc.CITY, "");

		spinnerCity.setOnItemSelectedListener(this);
		arrayListID = new ArrayList<String>();
		arrayListName = new ArrayList<String>();

		requestForCities(Web.CITY);

		arrayListName.add(deafaultCity);
		arrayListID.add(cityID_);
		sAdapter = new SpinnerAdapter_(getActivity(),
				R.layout.layout_spinner_item_, arrayListName);

		spinnerCity.setAdapter(sAdapter);
		sAdapter.notifyDataSetChanged();

		buttonSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (selectedCity.contains("Seleccionar ciudad")) {
					showAlertDialog(getActivity(), "",
							"Por favor seleccione su ciudad", true);
				} else {
					postData();
				}

			}
		});

		return rootView;
	}

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

			arrayListName.clear();
			arrayListID.clear();

			arrayListID.add("0000");

			arrayListName.add("Seleccionar ciudad");

			JSONArray array = new JSONArray(response);

			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				id = jsonObject.getString("id");
				name = jsonObject.getString("name");

				arrayListID.add(id);

				arrayListName.add(name);

			}

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

			tv.setTextSize(16);

			if (position == 0) {
				tv.setTextColor(Color.BLACK);
			}
			return convertView;
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		cityId = arrayListID.get(position);
		selectedCity = parent.getItemAtPosition(position).toString();

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	protected void postData() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("customerid", mID);
		params.put("city", selectedCity);
		Editor editor = mSharedPreferences.edit();
		editor.putString(BiteBc.CITY, selectedCity);
		editor.putString(BiteBc.CITY_ID, cityId);
		editor.commit();

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
						Log.e("CHECK Register Response:" + "", response);
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

	public void showAlertDialog(Context context, String title, String message,
			boolean error) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

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

}
