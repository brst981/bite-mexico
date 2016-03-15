package com.brst.android.bite.app.membership;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.User;
import com.brst.android.bite.app.home.HomeActivity;
import com.brst.android.bite.app.setting.TermAndConditionFragment;
import com.brst.android.bite.app.util.LogMsg;
import com.brst.android.bite.app.util.UI;

public class MemberShipSelectFragmentWithoutFacebook extends Fragment implements
		OnClickListener {
	Button btnPlaceOrder;
	CheckBox terms;

	private static String TAG = "MemberShipStepOneFragment";

	SharedPreferences sharedpreferences;

	public static final String ARG_PLAN_ID = "id";
	public static final String ARG_NAME = "name";
	public static final String ARG_PRICE = "price";
	public static final String ARG_SUB_TOTAL = "subTotal";
	static String mString_price;
	static String mString_message;
	TextView textUserName;
	TextView textEmail, termandcondition;

	TextView txtPlanName, txtplanPrice, planTimePeriod, txtsubTotalPrice,
			totalValue;
	UserDataHandler uDataHandler;

	User user;

	String planID, planName, planPrice, subTotalPrice;

	public static MemberShipSelectFragmentWithoutFacebook create(String id,
			String name, String price, String subTotal) {
		MemberShipSelectFragmentWithoutFacebook fragment = new MemberShipSelectFragmentWithoutFacebook();
		Bundle args = new Bundle();
		args.putString(ARG_PLAN_ID, id);
		args.putString(ARG_NAME, name);
		args.putString(ARG_PRICE, price);
		args.putString(ARG_SUB_TOTAL, subTotal);
		fragment.setArguments(args);
		mString_price = price;
		mString_message = name;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("free plan","plan");
	
		planID = getArguments().getString(ARG_PLAN_ID);
		planName = getArguments().getString(ARG_NAME);
		planPrice = getArguments().getString(ARG_PRICE);
		subTotalPrice = getArguments().getString(ARG_SUB_TOTAL);
		uDataHandler = UserDataHandler.getInstance();
		user = uDataHandler.getUser();
		sharedpreferences = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(
				R.layout.fragment_without_facebooklogin, container, false);

		btnPlaceOrder = (Button) rootView.findViewById(R.id.btn_place_order);
		btnPlaceOrder.setOnClickListener(this);

		textUserName = (TextView) rootView.findViewById(R.id.text_user_name);
		textEmail = (TextView) rootView.findViewById(R.id.text_email);
		termandcondition = (TextView) rootView.findViewById(R.id.t_n_c);
		textUserName.setText(sharedpreferences.getString(BiteBc.FIRSTNAME, "")
				+ " " + sharedpreferences.getString(BiteBc.LASTNAME, ""));
		textEmail.setText(sharedpreferences.getString(BiteBc.USER_ID, ""));

		txtPlanName = (TextView) rootView.findViewById(R.id.planName);
		txtPlanName.setText(planName);
		txtplanPrice = (TextView) rootView.findViewById(R.id.planPrice);
		txtplanPrice.setText(planPrice);
		planTimePeriod = (TextView) rootView.findViewById(R.id.planTimePeriod);
		String time = (!planID.equals("5")) ? planID.equals("4") ? planID.equals("209")?"1 Month"
				: "3 month" : "30 day":"12 Month";

		planTimePeriod.setText(time);
		txtsubTotalPrice = (TextView) rootView.findViewById(R.id.subTotalPrice);
		txtsubTotalPrice.setText(subTotalPrice);
		totalValue = (TextView) rootView.findViewById(R.id.totalValue);
		totalValue.setText(subTotalPrice);

		terms = (CheckBox) rootView.findViewById(R.id.terms);

		termandcondition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.setCustomAnimations(R.anim.slide_in_left,
						R.anim.slide_out_right);
				ft.replace(R.id.container, new TermAndConditionFragment());
				ft.addToBackStack(null);
				ft.commit();

			}
		});

		rootView.findViewById(R.id.btn_header_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						getActivity().onBackPressed();
						// hideKeyboard();

					}
				});

		return rootView;
	}

	private void makeRequestToBuyPlan(String api, final String planId,
			final String custId) {

		String url = Web.HOST + api;
		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG + "On Response", response);
						parsePlans(response);
					}
				}, new com.android.volley.Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d(TAG, "Error: " + error.getMessage());
						UI.hideProgressDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				Map<String, String> params = new HashMap<String, String>();
				params.put(DataKey.PLANID, planId);
				params.put(DataKey.CUST_ID, custId);
				params.put(DataKey.TOKEN_ID, "test123");

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				DataKey.TAG_JSON_OBJECT);

	}

	protected void parsePlans(String response) {

		UI.hideProgressDialog();

		try {
			JSONObject jsonOBject = new JSONObject(response);
			if (jsonOBject.getBoolean("success")) {

				// Intent intent = new Intent(getActivity(),
				// UserGuideActivity.class);
				// intent.putExtra(BiteBc.USER_PLAN, planID);
				// intent.putExtra("Plan_message", mString_message);
				// intent.putExtra("Plan_amount", mString_price);
				// startActivity(intent);
				// getActivity().overridePendingTransition(R.anim.slide_in_left,
				// R.anim.slide_out_right);
				// getActivity().finish();
				Editor editor = sharedpreferences.edit();
				editor.putString(BiteBc.USER_PLAN, planID);
				editor.commit();

				Intent intent = new Intent(getActivity(), HomeActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				getActivity().finish();

			} else {
				LogMsg.LOG(getActivity(), "Failed To Registered");
			}

		} catch (JSONException e) {
			Log.d(TAG, e.getMessage());
		}

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.btn_place_order:
			if (terms.isChecked()) {
				makeRequestToBuyPlan(WSConstant.Web.BUY, planID,
						user.getCustomerid());
			} else {
				LogMsg.showAlertDialog(getActivity(), "Validation error",
						"Please check terms and conditions", true);
				// LogMsg.LOG(getActivity(),
				// "Please check terms and conditions");
			}
			break;

		default:
			break;
		}

	}

	private void hideKeyboard() {
		// TODO Auto-generated method stub
		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
