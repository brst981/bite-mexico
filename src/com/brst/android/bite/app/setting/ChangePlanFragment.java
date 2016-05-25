package com.brst.android.bite.app.setting;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.control.UserDataHandler;
import com.brst.android.bite.app.domain.User;
import com.brst.android.bite.app.util.UI;

public class ChangePlanFragment extends Fragment {

	private static String TAG = "ChangePlan";

	TextView plan1, plan6, plan12,plan30, remainingTime;
	CheckBox chk1, chk6, chk12,chk30;

	UserDataHandler uDataHandler;
	User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uDataHandler = UserDataHandler.getInstance();
		user = uDataHandler.getUser();
	}

	// <!-- plan_name_1 plan_name_6 plan_name_12 chk1 chk6 chk12 -->
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("change plann","change paln");
		View rootView = inflater.inflate(R.layout.fragment_layout_change_plan,
				container, false);
		((TextView) rootView.findViewById(R.id.header_title))
				.setText(R.string.setting_change_plan);
		plan1 = (TextView) rootView.findViewById(R.id.plan_name_1);
		plan6 = (TextView) rootView.findViewById(R.id.plan_name_6);
		plan12 = (TextView) rootView.findViewById(R.id.plan_name_12);
		plan30= (TextView)rootView.findViewById(R.id.plan_name_30);
		remainingTime = (TextView) rootView.findViewById(R.id.remainingTime);

		chk1 = (CheckBox) rootView.findViewById(R.id.chk1);
		chk6 = (CheckBox) rootView.findViewById(R.id.chk6);
		chk12 = (CheckBox) rootView.findViewById(R.id.chk12);
		chk30= (CheckBox) rootView.findViewById(R.id.chk30);
		makeRequestForPlanInfo(Web.CHECKIN, user.getCustomerid());

		return rootView;
	}

	/**
	 * service to fetch checking detail
	 * 
	 */

	private void makeRequestForPlanInfo(String checkin, final String id) {

		String url = Web.HOST + checkin;
		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e(TAG + "On Response", response);
						// msgResponse.setText(response.toString());
						parseResponse(response);
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
				params.put(DataKey.CUSTOMER_ID, id);
				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				DataKey.TAG_JSON_OBJECT);

	}

	protected void parseResponse(String response) {

		String validity;
		String product_code = null;
		int daysLeft = 0;
		try {
			JSONObject jsonOBject = new JSONObject(response);
			Log.e("responseee",response);

			if (jsonOBject.getBoolean("success")) {
				product_code = jsonOBject.getString("planid");
				daysLeft = jsonOBject.getInt("daysleft");
				validity = "Días para que expire - "+daysLeft;
			} else {
				product_code = "";
				validity = "No se encontró Plano de miembros";
				daysLeft = 0;
			}

		} catch (JSONException e) {
			product_code = "";
			validity = "No se encontró Plano de miembros";
			daysLeft = 0;
			Log.e(TAG, e.getMessage());
		}
   
		if (!product_code.equals("")) {

			int product_id = Integer.parseInt(product_code);
			switch (product_id) {
			/*case 3:
				chk1.setChecked(true);
				break;*/
			case 184:
				chk30.setChecked(true);
				break;
			case 185:
				chk6.setChecked(true);
				break;
			case 183:
				chk12.setChecked(true);
				break;

			default:
				break;
			}

		}
		remainingTime.setText(validity);
		UI.hideProgressDialog();

	}
}
