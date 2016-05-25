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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
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
import com.brst.android.bitemaxico.app.R;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class MemberShipStepOneFragment extends Fragment implements
		OnClickListener {
	Button btnPlaceOrder;
	CheckBox terms;

	private static String TAG = "MemberShipStepOneFragment";

	SharedPreferences sharedpreferences;

	public static final String ARG_PLAN_ID = "id";
	public static final String ARG_NAME = "name";
	public static final String ARG_PRICE = "price";
	public static final String ARG_SUB_TOTAL = "subTotal";
	public static  LinearLayout couponLayout;
	private UiLifecycleHelper uiHelper;

	TextView textUserName;
	TextView textEmail, termandcondition;

	TextView txtPlanName, txtplanPrice, planTimePeriod, txtsubTotalPrice,
			totalValue, applyCoupen;
	EditText editCoupenCode;
	static String mString_price;
	static String mString_message;
	User user;
	UserDataHandler uDataHandler;

	String planID, planName, planPrice, subTotalPrice;
	boolean isCouponApplied = false;
	boolean isCouponForFree = false;
	String couponcode;
	View rootView;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	public static MemberShipStepOneFragment create(String id, String name,
			String price, String subTotal) {
		Log.e("free accc","freee accc");
		MemberShipStepOneFragment fragment = new MemberShipStepOneFragment();
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
		// View rootView = inflater.inflate(R.layout.fragment_layout_step_one,
		// container, false);

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_layout_step_one,
					container, false);
		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
			return rootView;
		}
		couponLayout=(LinearLayout)rootView.findViewById(R.id.couponLayout);
		Log.e("total",""+subTotalPrice);

		if(subTotalPrice.equals("$0.00"))
		{
			isCouponForFree=true;
			couponLayout.setVisibility(View.GONE);
			
		}
		uiHelper = new UiLifecycleHelper(getActivity(), callback);

		btnPlaceOrder = (Button) rootView.findViewById(R.id.btn_place_order);
		btnPlaceOrder.setOnClickListener(this);
		textUserName = (TextView) rootView.findViewById(R.id.text_user_name);
		textEmail = (TextView) rootView.findViewById(R.id.text_email);
		termandcondition = (TextView) rootView.findViewById(R.id.t_n_c);
		textUserName.setText(sharedpreferences.getString(BiteBc.FIRSTNAME, "")
				+ " " + sharedpreferences.getString(BiteBc.LASTNAME, ""));
		textEmail.setText(sharedpreferences.getString(BiteBc.USER_ID_FINAL, ""));
		// textUserName.setText(user.getName());
		// textEmail.setText(user.getEmail());

		txtPlanName = (TextView) rootView.findViewById(R.id.planName);
		txtPlanName.setText(planName);
		txtplanPrice = (TextView) rootView.findViewById(R.id.planPrice);
		txtplanPrice.setText(planPrice);
		planTimePeriod = (TextView) rootView.findViewById(R.id.planTimePeriod);
		
		/*String time = (!planID.equals("5")) ? planID.equals("4") ? "1 mes"
				: "1 mes" : "12 meses";*/
		String time="";
		
		if(planID.equals("183"))
			time="12 meses";
		else if(planID.equals("184"))
		time="30 dias";
		else
		time="1 mes";		
		planTimePeriod.setText(time);
		txtsubTotalPrice = (TextView) rootView.findViewById(R.id.subTotalPrice);
		txtsubTotalPrice.setText(subTotalPrice);
		totalValue = (TextView) rootView.findViewById(R.id.totalValue);
		applyCoupen = (TextView) rootView.findViewById(R.id.apply_coupn);
		editCoupenCode = (EditText) rootView.findViewById(R.id.edt_coupon_code);
		applyCoupen.setOnClickListener(this);
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

	private void makeRequestToApplyCoupon(String couponApi,
			final String planId, final String couponCodeVal) {
		String url = Web.HOST + couponApi;
		UI.showProgressDialog(getActivity());
		StringRequest strReq = new StringRequest(Method.POST, url,
				new com.android.volley.Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG + "On Response", response);
						parseResponseCoupen(response);
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
				params.put(DataKey.COUPON, couponCodeVal);
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
			Log.e("final resposne of free",response);
			JSONObject jsonOBject = new JSONObject(response);
			if (jsonOBject.getBoolean("success")) {

				Editor editor = sharedpreferences.edit();
				editor.putString(BiteBc.USER_PLAN, planID);
				editor.commit();

				Intent intent = new Intent(getActivity(), HomeActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				getActivity().finish();

			} else {
				LogMsg.LOG(getActivity(), "No se ha podido registrada");
			}

		} catch (JSONException e) {
			Log.d(TAG, e.getMessage());
		}

	}

	protected void parseResponseCoupen(String response) {

		UI.hideProgressDialog();

		try {
			JSONObject jsonOBject = new JSONObject(response);
			if (jsonOBject.getBoolean("success")) {
				String discountedPrice = jsonOBject.getString("subtotal");
				totalValue.setText(discountedPrice);
				txtsubTotalPrice.setText(discountedPrice);
				isCouponApplied = true;
				if (discountedPrice.equals("$0.00")) {
					isCouponForFree = true;
				}

				LogMsg.showAlertDialog_(getActivity(), "éxito",
						"Cupón aplicado con éxito", true);

			} else {

				LogMsg.showAlertDialog(getActivity(), "Código no válido",
						"Por favor, introduzca el código de cupón válido", true);

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
			Log.e("btn clkk",""+isCouponForFree);
			if (terms.isChecked()) {

				Fragment fragment;
				if (!isCouponApplied) {
					couponcode = "";
				}

				if (isCouponForFree) {
					//if(couponcode.equals(""))
						//publishStory();					
					//else
						makeRequestToBuyPlan(WSConstant.Web.BUY, planID,
								user.getCustomerid());
					
					Log.e("isCouponforfree",""+isCouponForFree);
					Log.e("isCouponApplied",""+isCouponApplied);
					Log.e("couponcode",""+couponcode);

					
					//if(isCouponApplied)
						
					//publishStory();
					
					
				} else {
					fragment = MemberShipStepTwoFragment.create(planID,
							couponcode);
					FragmentManager fm = this.getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ft.setCustomAnimations(R.anim.slide_in_left,
							R.anim.slide_out_right);
					ft.replace(R.id.container, fragment);
					ft.addToBackStack(null);
					ft.commit();
				}

			} else {
				LogMsg.showAlertDialog(getActivity(), "error de validación",
						"Por favor, consulte los términos y condiciones", true);
				// LogMsg.LOG(getActivity(),
				// "Please check terms and conditions");
			}
			break;

		case R.id.apply_coupn:
			couponcode = editCoupenCode.getText().toString();
			if (isCouponForFree) {
				isCouponForFree = false;
			}
			if (!TextUtils.isEmpty(couponcode))
				makeRequestToApplyCoupon(WSConstant.Web.COUPON, planID,
						couponcode);
			hideKeyboard();
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
	private void publishStory() {
		try {
			// if (mSharedPreferences != null
			// && mSharedPreferences.getString("WITHOUT_FACEBOOK", "")
			// .matches("301")) {

			// publishFeedDialog();
			// } else {
			Session session = Session.getActiveSession();
			//String picture="http:\/\/api.bitemexico.com\/media\/catalog\/product\/cache\/1\/image\/265x\/9df78eab33525d08d6e5fb8d27136e95\/a\/l\/aloha_1.jpg"

			if (session != null) {

				if (FacebookDialog.canPresentShareDialog(getActivity(),
						FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
					FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
							getActivity()).setName(getMessage())
							.setLink("http://www.bitebc.ca")
							.setDescription(getDescription())
							.setCaption("Desarrollado por mordedura Bc")
							.setPicture("http://api.bitemexico.com/media/catalog/product/cache/1/image/265x/9df78eab33525d08d6e5fb8d27136e95/a/l/aloha.jpg").build();
					uiHelper.trackPendingDialogCall(shareDialog.present());
					Log.e("sesson","session");

				} /*else if (mSharedPreferences != null
						&& mSharedPreferences.getString("WITHOUT_FACEBOOK", "")
								.matches("301")) {

					FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
							getActivity()).setName(getMessage())
							.setLink("http://www.bitebc.ca")
							.setDescription(getDescription())
							.setCaption("Desarrollado por mordedura Bc")
							.setPicture("HTTTPP").build();
					uiHelper.trackPendingDialogCall(shareDialog.present());
				}*/ else {
					Log.e("sesson1","session1");

					publishFeedDialog();
				}

				// }

			}

		} catch (Exception ex) {

		}

}
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
				Toast.makeText(getActivity(), "Successfully Posted",
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Restaurant successfully shared");
			} else if (postId.equalsIgnoreCase("cancel")) {
				Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT)
						.show();

			}
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (session != null && session.isOpened()) {
			//shareButton.setVisibility(View.VISIBLE);
		} else {
			//shareButton.setVisibility(View.INVISIBLE);
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
		//outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
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
	private String getMessage() {
		String message;
		/*message = String.valueOf("Disfrutando "
				+ getOffer(restaurant.getOfferAvailable()) + " a "
				+ restaurant.getName() + " gracias a morder antes de Cristo.");*/
		
		message="Bite Maxico";
		return message;

	}

	private String getDescription() {
		String desc;
		desc = "Una membresia bite mexico te ofrece el 50% de descuento o el 2 x 1 en la cuenta total de tus alimentos en nuestros restaurantes participantes.";
		return desc;
	}
	private void publishFeedDialog() {
		Bundle params = new Bundle();
		params.putString("name", getMessage());
		params.putString("caption", "Desarrollado por mordedura Bc");
		params.putString("description", getDescription());
		params.putString("link", "http://www.bitebc.ca");
		params.putString("picture", "http://api.bitemexico.com/media/catalog/product/cache/1/image/265x/9df78eab33525d08d6e5fb8d27136e95/a/l/aloha.jpg");

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
								makeRequestToBuyPlan(WSConstant.Web.BUY, planID,
										user.getCustomerid());
								Toast.makeText(getActivity(),
										"Publicado éxito",
										Toast.LENGTH_SHORT).show();
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
}
