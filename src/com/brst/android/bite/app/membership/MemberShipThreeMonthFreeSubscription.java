package com.brst.android.bite.app.membership;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import android.widget.Toast;

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
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class MemberShipThreeMonthFreeSubscription extends Fragment implements
		OnClickListener {
	Button shareButton;
	CheckBox terms;

	private static String TAG = "MemberShipStepOneFragment";

	SharedPreferences sharedpreferences;

	public static final String ARG_PLAN_ID = "id";
	public static final String ARG_NAME = "name";
	public static final String ARG_PRICE = "price";
	public static final String ARG_SUB_TOTAL = "subTotal";

	TextView termandcondition;

	UserDataHandler uDataHandler;

	User user;
	static String mString_price;
	static String mString_message;
	String planID, planName, planPrice, subTotalPrice;

	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	public static MemberShipThreeMonthFreeSubscription create(String id,
			String name, String price, String subTotal) {
		MemberShipThreeMonthFreeSubscription fragment = new MemberShipThreeMonthFreeSubscription();
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
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
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
				R.layout.fragment_layout_three_moth_sub, container, false);

		shareButton = (Button) rootView.findViewById(R.id.shareFb);

		termandcondition = (TextView) rootView.findViewById(R.id.t_n_c);

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
		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (terms.isChecked()) {
					publishStory();
				} else {
					LogMsg.showAlertDialog(getActivity(), "Error",
							"Por favor, acepta los términos y condiciones", true);
				}
			}
		});
		return rootView;
	}
	/*
	 * Facebook call back
	 */

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d(TAG, String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d(TAG, "Success!");
			final String postId = data
					.getString("com.facebook.platform.extra.COMPLETION_GESTURE");
			if (postId.equalsIgnoreCase("post")) {
				// Toast.makeText(getActivity(),
				// "Restaurant successfully shared",
				// Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Restaurant successfully shared");
				makeRequestToBuyPlan(WSConstant.Web.BUY, planID,
						user.getCustomerid());
			} else if (postId.equalsIgnoreCase("cancel")) {
				Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT)
						.show();

			}
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (session != null && session.isOpened()) {
			shareButton.setVisibility(View.VISIBLE);
		} else {
			shareButton.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private void publishStory() {
		Session session = Session.getActiveSession();

		if (session != null) {

			if (FacebookDialog.canPresentShareDialog(getActivity(),
					FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
				FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
						getActivity())
						.setName(getMessage())
						.setLink("http://www.bitebc.ca")
						.setDescription(" ")
						.setCaption("Desarrollado por mordedura de México")
						.setPicture(
								"http://api.bitemexico.com/api/final.png")
								
						.build();
				uiHelper.trackPendingDialogCall(shareDialog.present());

			} else {
				publishFeedDialog();
			}

		}

	}

	private void publishFeedDialog() {
		Bundle params = new Bundle();
		params.putString("name", getMessage());
		params.putString("caption", "Desarrollado por Bite Mexico");
		params.putString("description"," ");
		params.putString("link", "http://www.bitebc.ca");
		params.putString("picture",
				"http://api.bitemexico.com/api/final.png");

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(getActivity(),
				Session.getActiveSession(), params)).setOnCompleteListener(
				new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(getActivity(),
										"Publicado éxito",
										Toast.LENGTH_SHORT).show();
								Log.d(TAG, "Restaurant successfully shared");
								makeRequestToBuyPlan(WSConstant.Web.BUY,
										planID, user.getCustomerid());
							} else {
								// User clicked the Cancel button
								Toast.makeText(
										getActivity().getApplicationContext(),
										"Cancelado", Toast.LENGTH_SHORT).show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Cancelado", Toast.LENGTH_SHORT).show();
						} else {
							// Generic, ex: network error
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Error", Toast.LENGTH_SHORT).show();
						}
					}

				}).build();
		feedDialog.show();
	}

	private String getMessage() {
		String message = null;
		message = "Solo descarga tu App Bite México y consigue 50% o 2x1 en alimentos en restaurantes participantes!";
		return message;

	}

	private String getDescription() {
		String desc;
		desc = "Bite México es un gran escala comensales club en la forma de una aplicación móvil proporciona a los miembros descuentos en comer fuera. Con nuestra aplicación intuitiva usted será capaz de localizar todos nuestros restaurantes participantes con un solo toque de un botón y ahorrar cientos de dólares en el transcurso del año.";
		return desc;
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
				Log.d(TAG, planID + "AND User id:" + user.getCustomerid());
				if (Integer.parseInt(planID) == 3) {
					makeRequestToBuyPlan(WSConstant.Web.BUY, planID,
							user.getCustomerid());
				} else {
					FragmentManager fm = this.getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ft.setCustomAnimations(R.anim.slide_in_left,
							R.anim.slide_out_right);
					ft.replace(R.id.container,
							MemberShipStepTwoFragment.create(planID, ""));
					ft.addToBackStack(null);
					ft.commit();
				}
			} else {
				LogMsg.showAlertDialog(getActivity(), "Validation error",
						"Por favor, consulte los términos y condiciones", true);
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
