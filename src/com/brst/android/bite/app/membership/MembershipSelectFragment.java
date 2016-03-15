package com.brst.android.bite.app.membership;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.brst.android.bitemaxico.app.R;
import com.brst.android.bite.app.base.BaseContainerFragment;
import com.brst.android.bite.app.constant.AppConstant.BiteBc;
import com.brst.android.bite.app.constant.WSConstant.DataKey;
import com.brst.android.bite.app.constant.WSConstant.Web;
import com.brst.android.bite.app.control.AppController;
import com.brst.android.bite.app.domain.Plan;
import com.brst.android.bite.app.util.JsonParser;
import com.brst.android.bite.app.util.UI;

public class MembershipSelectFragment extends BaseContainerFragment implements
		OnItemClickListener {
	public MembershipActivity mActivity;
	private static String TAG = "MembershipSelectFragment";
	SharedPreferences sharedpreferences;

	ListView listPlans;

	ListPlanAdapter adapter;

	List<Plan> listItems;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	

		mActivity = (MembershipActivity) this.getActivity();
		listItems = new ArrayList<Plan>();
		sharedpreferences = getActivity().getSharedPreferences(
				BiteBc.MyPREFERENCES, Context.MODE_PRIVATE);
		String cusId = sharedpreferences.getString(BiteBc.CUST_ID, "");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("customerid", cusId);
		makeRequestForPlan(Web.PLANS, params);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_layout_select_plan,
				container, false);
		listPlans = (ListView) rootView.findViewById(R.id.list_plans);
		listPlans.setOnItemClickListener(this);
		adapter = new ListPlanAdapter();
		listPlans.setAdapter(adapter);

		return rootView;
	}

	class ListPlanAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		@Override
		public int getCount() {
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			return listItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (inflater == null)
				inflater = (LayoutInflater) getActivity().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_row_plan, null);
			}
			TextView pName = (TextView) convertView
					.findViewById(R.id.plan_name);
			TextView pPrice = (TextView) convertView
					.findViewById(R.id.plan_price);

			Plan item = listItems.get(position);
			pName.setText(item.getPlanName());
			pPrice.setText(item.getPrice());
			return convertView;

		}
	}

	private void makeRequestForPlan(String product,
			final HashMap<String, String> rParams) {

		String url = Web.HOST + product;
		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e(TAG + "On Response", response);
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
			protected Map<String, String> getParams() {
				Map<String, String> params = rParams;
				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq,
				DataKey.TAG_JSON_OBJECT);

	}

	protected void parsePlans(String response) {
		UI.hideProgressDialog();
		listItems = JsonParser.getPlanForNewUsers(response);
		// adapter = new ListPlanAdapter();
		// listPlans.setAdapter(adapter);
		adapter.notifyDataSetChanged();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Fragment fragment = null;
		String pId, pName, pPrice, subtotalPrice;
		pId = listItems.get(position).getPlanId();
		pName = listItems.get(position).getPlanName();
		pPrice = listItems.get(position).getPrice();
		subtotalPrice = listItems.get(position).getSubTotalprice();
		Log.e("pId", ""+pId);

		if (sharedpreferences.getString("WITHOUT_FACEBOOK", "").matches("301")) {
			if (pId.equalsIgnoreCase("3") || pId.equalsIgnoreCase("184")) {
				//fragment = MemberShipSelectFragmentWithoutFacebook.create(pId,
						//pName, pPrice, subtotalPrice);
				
				Log.e("free","free");
				fragment = MemberShipThreeMonthFreeSubscription.create(pId,
				 pName,
				 pPrice, subtotalPrice);

			} else {
				fragment = MemberShipStepOneFragment.create(pId, pName, pPrice,
						subtotalPrice);
				Log.e("ont step","one step");


			}
			mActivity.currentFragment = fragment;
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
			ft.replace(R.id.container, fragment);
			ft.addToBackStack(null);
			ft.commit();
		} else {
			if (pId.equalsIgnoreCase("3") || pId.equalsIgnoreCase("184")) {
				//fragment = MemberShipSelectFragmentWithoutFacebook.create(pId,
						//pName, pPrice, subtotalPrice);
				Log.e("free1","free1");

				 fragment = MemberShipThreeMonthFreeSubscription.create(pId,
				 pName, pPrice, subtotalPrice);

			} else {

				fragment = MemberShipStepOneFragment.create(pId, pName, pPrice,
						subtotalPrice);
				Log.e("ont step1","one step1");

			}
			mActivity.currentFragment = fragment;
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
			ft.replace(R.id.container, fragment);
			ft.addToBackStack(null);
			ft.commit();
		}

	}

}
